package ontology.avatar;

import java.awt.Dimension;
import java.util.ArrayList;

import core.VGDLSprite;
import core.competition.CompetitionParameters;
import core.content.SpriteContent;
import core.game.Game;
import core.player.AbstractPlayer;
import core.player.Player;
import ontology.Types;
import tools.*;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 22/10/13
 * Time: 18:04
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class MovingAvatar extends VGDLSprite {

    public boolean alternate_keys;
    public ArrayList<Types.ACTIONS> actions;
    public ArrayList<Types.ACTIONS> actionsNIL;
    public Player player;

    private KeyHandler ki = CompetitionParameters.KEY_HANDLER == CompetitionParameters.KEY_INPUT ?
            new KeyInput() : new KeyPulse();

    public Types.MOVEMENT lastMovementType = Types.MOVEMENT.STILL;

    public MovingAvatar() {
    }

    public MovingAvatar(Vector2d position, Dimension size, SpriteContent cnt) {
        //Init the sprite
        this.init(position, size);

        //Specific class default parameter values.
        loadDefaults();

        //Parse the arguments.
        this.parseParameters(cnt);
    }

    protected void loadDefaults() {
        super.loadDefaults();
        actions = new ArrayList<Types.ACTIONS>();
        actionsNIL = new ArrayList<Types.ACTIONS>();

        color = Types.WHITE;
        speed = 1;
        is_avatar = true;
        alternate_keys = false;
    }

    public void postProcess() {

        //Define actions here first.
        if(actions.size()==0)
        {
            actions.add(Types.ACTIONS.ACTION_LEFT);
            actions.add(Types.ACTIONS.ACTION_RIGHT);
            actions.add(Types.ACTIONS.ACTION_DOWN);
            actions.add(Types.ACTIONS.ACTION_UP);
        }

        super.postProcess();

        //A separate array with the same actions, plus NIL.
        for(Types.ACTIONS act : actions)
        {
            actionsNIL.add(act);
        }
        actionsNIL.add(Types.ACTIONS.ACTION_NIL);
    }

    /**
     * This update call is for the game tick() loop.
     * @param game current state of the game.
     */
    public void update(Game game) {
        lastMovementType = Types.MOVEMENT.STILL;

        //Sets the input mask for this cycle.
        ki.setMask();

        //Get the input from the player.
        requestPlayerInput(game);

        //Map from the action mask to a Vector2D action.
        Vector2d action2D = Utils.processMovementActionKeys(ki.getMask());

        //Apply the physical movement.
        applyMovement(game, action2D);
    }


    /**
     * This move call is for the Forward Model tick() loop.
     * @param game current state of the game.
     * @param actionMask action to apply.
     */
    public void move(Game game, boolean[] actionMask) {

        //Apply action supplied (active movement). USE is checked up in the hierarchy.
        Vector2d action = Utils.processMovementActionKeys(actionMask);
        applyMovement(game, action);
    }

    private void applyMovement(Game game, Vector2d action)
    {
        lastMovementType = this.physics.activeMovement(this, action, this.speed);
    }

    /**
     * Requests the controller's input, setting the game.ki.action mask with the processed data.
     * @param game
     */
    protected void requestPlayerInput(Game game)
    {
        ElapsedCpuTimer ect = new ElapsedCpuTimer(CompetitionParameters.TIMER_TYPE);
        ect.setMaxTimeMillis(CompetitionParameters.ACTION_TIME);

        VGDLSprite.loadImages = false;	// don't need to load images whilst the agent is thinking
        Types.ACTIONS action = this.player.act(game.getObservation(), ect.copy());
        VGDLSprite.loadImages = true;	// need to load images again for the real game

        if(ect.exceededMaxTime())
        {
            long exceeded =  - ect.remainingTimeMillis();

            if(ect.elapsedMillis() > CompetitionParameters.ACTION_TIME_DISQ)
            {
                //The agent took too long to replay. The game is over and the agent is disqualified
                System.out.println("Too long: " + "(exceeding "+(exceeded)+"ms): controller disqualified.");
                game.disqualify();
            }else{
                System.out.println("Overspent: " + "(exceeding "+(exceeded)+"ms): applying ACTION_NIL.");
            }

            action = Types.ACTIONS.ACTION_NIL;
        }


        if(!actions.contains(action))
            action = Types.ACTIONS.ACTION_NIL;

        this.player.logAction(action);
        game.setAvatarLastAction(action);
        ki.reset();
        ki.setAction(action);
    }


    public void updateUse(Game game)
    {
        //Nothing to do by default.
    }

    /**
     * Gets the key handler of this avatar.
     * @return - KeyHandler object.
     */
    public KeyHandler getKeyHandler() { return ki; }

    /**
     * Sets the key handler of this avatar.
     * @param k - new KeyHandler object.
     */
    public void setKeyHandler(KeyHandler k) { ki = k; }


    public VGDLSprite copy() {
        MovingAvatar newSprite = new MovingAvatar();
        this.copyTo(newSprite);

        //copy player
        try {
            newSprite.player = player.copy();
        } catch (Exception e) {e.printStackTrace();}

        //copy key handler
        newSprite.setKeyHandler(this.getKeyHandler());

        return newSprite;
    }

    public void copyTo(VGDLSprite target) {
        MovingAvatar targetSprite = (MovingAvatar) target;
        targetSprite.alternate_keys = this.alternate_keys;
        targetSprite.actions = new ArrayList<Types.ACTIONS>();
        targetSprite.actionsNIL = new ArrayList<Types.ACTIONS>();
        targetSprite.postProcess();
        super.copyTo(targetSprite);
    }

}
