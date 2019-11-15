/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package penguin_fsm;
import java.util.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * PENGUIN FSM:
 * Penguin FSM for Assignment 1. This program is a penguin where the user enters a 
 * 1 or 0 in response to prompts that control the time of day and the penguins actions
 * @author James Hayes
 */
public class Penguin_FSM {

     private static int health = 6; //penguins health
     private static int energy = 5; //penguins energy
     private static int previousState; //the previous state the penguin was in
    
    /**
     * Abstract base class of all states. Note that it has
     * been made static so we can use it in the static main
     * method. Do not change this!
     */
    public static abstract class State {
        protected Scanner input = new Scanner(System.in); //input
        protected Random random = new Random(); //random object to generate random number
        //methods of all states
        public State() {}
        public abstract void enter();
        public abstract void exit();
        public abstract int updateAndCheckTransitions();
        
        public int getHealth(){ //returns current health
            return health;
        }
        
        public int getEnergy(){ //returns current energy
            return energy;
        }
        
        public int CheckInternalState(){ //checks if penguin is hungry or tired
            if (health < 3){
                return 3;
            }
            else if (energy < 3){
                return 4;
            }
            else return 0;
        }
        
    }
    
    /**INTERNAL(EMOTIONAL) STATES **/
    /**
     * public class Hungry
     * This class/state is transitioned when the penguins health drops below 3
     * and is "hungry". THe user may prompt the penguin to eat and regain health
     */
    public static class Hungry extends State{
        
         public void Eat(){ //eat function
            health += 3;
        }
        
        @Override public void enter(){
            System.out.println("The penguin is hungry");
        }
        
        @Override public void exit() {            
            System.out.println("The penguin eats some fish and is no longer hungry.");
            Eat();
        }
        public int updateAndCheckTransitions() {        
            int eatInput;
            System.out.print("Should the penguin eat? (1 or 0)");
            eatInput = input.nextInt();
            if (eatInput == 0) {
                //the penguin does not eat
                System.out.println("The Penguin does not eat");
                return previousState;
            }
            else {
                exit();
                return previousState;
            } 
        }
    }
    /**
     * public class Sleepy
     * The penguin is transitioned into this state when it's energy drops below 
     * 3 or it is "sleepy". The user may prompt the penguin to nap to gain 3 
     * energy.
     */
    public static class Sleepy extends State{
         public void Nap(){ //nap function
            energy += 3;
        }
         @Override public void enter(){
            System.out.println("The penguin is sleepy!");
        }
        
        @Override public void exit() {            
            System.out.println("The penguin sleeps and feels rested.");
            Nap();
        }
        public int updateAndCheckTransitions() {        
            int sleepInput;
            System.out.print("Should the penguin sleep? (1 or 0)");
            sleepInput = input.nextInt();
            if (sleepInput == 0) {
                //the penguin does not eat
                System.out.println("The Penguin did not sleep");
                return previousState;
            }
            else {
                exit();
                return previousState;
            } 
        }
    } 
    
    /** ACTION STATES **/
    /**
     * public class FeedBabies
     * This is a random event "action" potentially prompted when it is morning. 
     * The user may prompt the penguin to feed it's babies. The penguin uses its
     * own health points to feed its babies
     */
    public static class FeedBabies extends State{
        int feedAmount;
         @Override public void enter(){ 
             System.out.println("The penguin begins to gather it's children for feeding");
         }
        
        @Override public void exit() {            
            System.out.println("The baby's penguins are fed but the mother penguin may be a little hungry now.");
        }
        public int updateAndCheckTransitions() {          
            System.out.println("How much should the penguin feed its babies? (must be less than health): ");
            feedAmount = input.nextInt();
            
            if(feedAmount >= health){ //check to make sure penguin has enough health
                System.out.println("You don't have enough health for that.");
                updateAndCheckTransitions();
            }
  
               try{
                System.out.println("The penguin begins to feed its babies...");
                health -= feedAmount;
                   TimeUnit.SECONDS.sleep(feedAmount); 
                }
                catch(InterruptedException e){
                    System.out.println("Please allow the penguin to feed its babies");
                }
                
                return previousState;
        }
    }
    /**
     * public class Hunting
     * This is a random event "action" that can potentially happen in the afternoon
     * state where the penguin goes exploring then gets attacked by a sea lion and the
     * user may prompt the penguin to fight back.
     */
    public static class Hunting extends State{
        int damageAmount, userInput;
         @Override public void enter(){ 
             System.out.println("The penguin goes exploring");
             try{
                TimeUnit.SECONDS.sleep(5); //pause
             }
             catch(InterruptedException e){
                System.out.println("Please allow the penguin to explore");
             }
             System.out.println("The penguin is being attacked by a sea lion!");
         }
        
        @Override public void exit() {            
            System.out.println("The penguin returns home.");
        }
        
        @Override public int updateAndCheckTransitions() {          
            System.out.println("Should the penguin attack the sea lion? (1 or 0): ");
            userInput = input.nextInt();
            
            if(userInput == 0){
                System.out.println("The penguin tries to escape but the sea lion attacks anyways!");
            }
            
            while(health > 0){
                System.out.println("The penguin tries to strike!");
                try{
                    TimeUnit.SECONDS.sleep(2); 
                }
                catch(InterruptedException e){
                    System.out.println("Please allow the penguin to explore");
                } 
                int randProb = random.nextInt(10) + 1; //generate a number between 1-10
                if(randProb <= 3){ //30% chance of knocking out sea lion
                    System.out.println("The penguin knocks out the sea lion!");
                    return previousState;
                }
                else{
                    System.out.println("The penguin misses the sea lion!"); 
                }  
            }
            
            System.out.println("The penguin was killed by the sea lion!");
            return previousState;
        }
    }
    
    
    /** TIME STATES **/
    /**
     * This class represents the state in which the flower
     * is closed for the night. It transitions to Morning
     * when it is light out.
     */
    
    public static class Night extends State {
        public Night() {
            super();
        }
        public void enter() {
            System.out.println("It is night, should the penguin sleep?.");
        }
        public void exit() {            
            System.out.println("The Penguin has slept, and it is morning.");
            //decrement health and add energy
            health -= 2;
            energy += 4;
            
        }
        public int updateAndCheckTransitions() {        
            int awake;
            System.out.print("Should the penguin sleep? (1 or 0) ");
            awake = input.nextInt();
            if (awake == 0) {
                System.out.println("The Penguin remains awake.");
                return 2;
            }
            else {
                return 0;
            } 
        }
    }
    
    /**
     * This class represents the state in which the flower is open and facing east. 
     * It transitions to Afternoon when the sun is to the west.
     */
    public static class Morning extends State {
        int randProb; //random probability for random even
        
        public Morning() {
            super();
        }
        
        public void enter() {
            System.out.println("It is morning and the penguin feels rested!");
        }
        public void exit() {            
            System.out.println("The day passes on.");
        }
        public int updateAndCheckTransitions() { 
            int day;
            //System.out.println("The penguin is feeling rested, but hungry");
            System.out.print("Is it still morning? (1 or 0): ");
            day = input.nextInt();
            
            // change to afternoon
            if (day == 0) return 1;
            // stay at morning
            else{
                randProb = random.nextInt(10) + 1; //generate a number between 1-10
                if(randProb <= 5 && health >= 3){ //50% chance of random event and only if enough health to feed
                    System.out.println("The penguin's babies seem hungry should it feed them? (1 or 0): ");
                    if((input.nextInt()) == 1){
                        previousState = 0; //set previous state to morning
                        return 5; //move on to feed babies state
                    }
                }
                return 0;
            }
        }
    }
    
    /**
     * This class represents the state in which the flower
     * is open and facing west. It transitions to Night
     * when it is no longer light for a time.
     */
    public static class Afternoon extends State {
	private int count; // How many turns has it been dark (for timeout)
        public Afternoon() {
            super();
        }
        
        public void enter() {
            System.out.println("It is now afternoon");
        }
        public void exit() {            
            System.out.println("The afternoon is moving on and it is getting dark...");
        }
        public int updateAndCheckTransitions() {             
            int response;
            System.out.println("The Penguin is chillin");
            
            // If it is raining, increment the static water level (using the
            // static water variable). If the flower was wilting before, it no
            // longer is.
            System.out.print("Is it still afternoon? (1 or 0):  ");
            response = input.nextInt();
            if (response == 0) {
                return 2;
            }                              
            else {   
                int randProb = random.nextInt(10) + 1; //generate a number between 1-10
                if(randProb <= 5 && energy >= 3){ //50% chance of random event and enough energy to fight
                    System.out.println("The penguin feels like exploring. Should the penguing go exploring? (1 or 0): ");
                    if((input.nextInt()) == 1){
                        previousState = 2; //set previous state to afternoon
                        return 6; //move on to hunting state
                    }
                }
                return 1;
            }

        }
    }
    
    public static void main(String[] args) {
        /* IMPORTANT! Change the 3 to the number of states you have*/
        int numberOfStates = 7;
        State[] states = new State[numberOfStates]; 

        /* IMPORTANT! Modify this code to create each of your states */
        states[0] = new Morning();
        states[1] = new Afternoon();
        states[2] = new Night();
        states[3] = new Hungry();
        states[4] = new Sleepy();
        states[5] = new FeedBabies();
        states[6] = new Hunting();

        /**** End of code to modify ****/

        int currentState = 0;
        int nextState;  
        
        //print energy and health stats
        System.out.println("-------------------\nHealth: " + states[currentState].getHealth() + " Energy: " + states[currentState].getEnergy() + "\n-------------------");
        states[0].enter();
        while(true) {
            nextState = states[currentState].updateAndCheckTransitions();
            //change states
            if (nextState != currentState) {
                //System.out.println("Changing states");
                
                if(nextState != 5 && nextState != 6){ //if not transitioning to an action state or back to previous state from an action or internal state
                   states[currentState].exit();
                   //decrement interal values
                   health--;
                   energy--;
                }
                currentState = nextState;
                if(previousState != currentState){ //if not transitioning to the old state from an action state
                   states[currentState].enter(); //display new state enter message
                }
                
            }
            
            //print energy and health stats
            System.out.println("-------------------\nHealth: " + states[currentState].getHealth() + " Energy: " + states[currentState].getEnergy() + "\n-------------------");
            
            if (currentState != 5 && currentState != 6){ //if not in the middle of an action state
                //check internal states
                if(states[currentState].CheckInternalState() == 3){ //penguin is hungry
                    previousState = currentState; //save state to return to
                    currentState = 3; //set to hungry state
                    states[currentState].enter(); //enter hungry state
                    currentState = states[currentState].updateAndCheckTransitions(); //update and return to state
                    //print energy and health stats
                    System.out.println("-------------------\nHealth: " + states[currentState].getHealth() + " Energy: " + states[currentState].getEnergy() + "\n-------------------");
                }
                else if(states[currentState].CheckInternalState() == 4){ //penguin is sleepy
                    previousState = currentState; //save state to return to
                    currentState = 4; //set to sleepy state
                    states[currentState].enter(); //enter sleepy state
                    currentState = states[currentState].updateAndCheckTransitions(); //update and return to state
                    //print energy and health stats
                    System.out.println("-------------------\nHealth: " + states[currentState].getHealth() + " Energy: " + states[currentState].getEnergy() + "\n-------------------");
                }
                
                
            }
            
            if(health < 0){
                System.out.println("The penguin health has dropped to low! The penguin eats some fish.");
                health = 1;
            }
            
        }
    }
    
//END CLASS    
}


