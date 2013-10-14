package pacman;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import pacman.controllers.Controller;
import pacman.controllers.HumanController;
import pacman.controllers.KeyBoardInput;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.controllers.examples.NearestPillPacMan;
import pacman.controllers.examples.NearestPillPacManVS;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.examples.RandomNonRevPacMan;
import pacman.controllers.examples.RandomPacMan;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.StarterPacMan;
import jumo.fsm.JumoFSM;
import jumo.mcts.JumoMCTS;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.internal.Ghost;

import static pacman.game.Constants.*;

/**
 * This class may be used to execute the game in timed or un-timed modes, with or without
 * visuals. Competitors should implement their controllers in game.entries.ghosts and 
 * game.entries.pacman respectively. The skeleton classes are already provided. The package
 * structure should not be changed (although you may create sub-packages in these packages).
 */
@SuppressWarnings("unused")
public class Executor
{
	/**
	 * The main method. Several options are listed - simply remove comments to use the option you want.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		Executor exec=new Executor();
		
		/*
		// Get data for the MLP training
		Controller<EnumMap<GHOST, MOVE>> ghostController = new Legacy2TheReckoning();
		exec.runGameTimed(new DataCollectorController(new KeyBoardInput(), ghostController.getClass().getSimpleName() + ".txt"), ghostController,visual); 
		//*/
		
		/* Run an evolution to find a genome for the FSM controller.
		 */
		/*
		int population_size = 100;
		int mutate_count = 20;
		int mutate_percentage = 25;
		int crossover_count = 20; // Must be less than or equal to (popsize - mutate - new_count) / 2
		int new_count = 20;
		int runs = 100;
		int games_per_run = 100;
		
		//exec.runEvolution(new Legacy2TheReckoning(), population_size, mutate_count, mutate_percentage, crossover_count, new_count, runs, games_per_run);
		//exec.runEvolution(new Legacy(), population_size, mutate_count, mutate_percentage, crossover_count, new_count, runs, games_per_run);
		exec.runEvolution(new StarterGhosts(), population_size, mutate_count, mutate_percentage, crossover_count, new_count, runs, games_per_run);
		//*/
		
		/*
		// Test the different controllers
		int numTrials=1000;
		Controller<EnumMap<GHOST,MOVE>> ghostsController = new Legacy2TheReckoning();
		//exec.runExperiment(new JumoFSM(JumoFSM.DEFAULT_GENOME_STARTER_GHOSTS), ghostsController, numTrials);
		//exec.runExperiment(new JumoFSM(JumoFSM.DEFAULT_GENOME_LEGACY), ghostsController, numTrials);
		//exec.runExperiment(new JumoFSM(JumoFSM.DEFAULT_GENOME_LEGACY2_THERECKONING), ghostsController, numTrials);
		//exec.runExperiment(new StarterPacMan(),new StarterGhosts(), numTrials);

		/*
		double C = JumoMCTS.DEFAULT_C_VALUE;
		if (args.length >= 1) {
			C = Double.parseDouble(args[0]);
			System.out.println("C: " + C);
		}
		
		if (args.length >= 2) {
			numTrials = Integer.parseInt(args[1]);
		}
		*/
		//exec.runExperiment(new JumoMCTS(C), new StarterGhosts(), numTrials);
		//*/
		
		/*
		//run a game in synchronous mode: game waits until controllers respond.
		int delay=5;
		boolean visual=true;
		exec.runGame(new RandomPacMan(),new RandomGhosts(),visual,delay);
  		//*/
		
		/*
		//run the game in asynchronous mode.
		boolean visual=true;
		//exec.runGameTimed(new JumoFSM(JumoFSM.DEFAULT_GENOME_LEGACY2_THERECKONING),new Legacy2TheReckoning(),visual);
		//exec.runGameTimed(new JumoFSM(JumoFSM.DEFAULT_GENOME_LEGACY),new Legacy(),visual);
		//exec.runGameTimed(new JumoFSM(JumoFSM.DEFAULT_GENOME_STARTER_GHOSTS),new StarterGhosts(),visual);
		//exec.runGameTimed(new JumoMCTS(),new StarterGhosts(),visual);
		//*/
		
		/*
		//run the game in asynchronous mode but advance as soon as both controllers are ready  - this is the mode of the competition.
		//time limit of DELAY ms still applies.
		boolean visual=true;
		boolean fixedTime=false;
		exec.runGameTimedSpeedOptimised(new JumoFSM(JumoFSM.DEFAULT_GENOME_LEGACY2_THERECKONING),new Legacy2TheReckoning(),fixedTime,visual);
		//*/
		
		/*
		//run game in asynchronous mode and record it to file for replay at a later stage.
		boolean visual = true;
		String fileName = "replay";
		String fileType = ".txt";
		exec.runGameTimedRecorded(new JumoFSM(JumoFSM.DEFAULT_GENOME_LEGACY2_THERECKONING), new Legacy2TheReckoning(), visual, fileName + "1" + fileType);
		exec.runGameTimedRecorded(new JumoFSM(JumoFSM.DEFAULT_GENOME_LEGACY2_THERECKONING), new Legacy2TheReckoning(), visual, fileName + "2" + fileType);
		exec.runGameTimedRecorded(new JumoFSM(JumoFSM.DEFAULT_GENOME_LEGACY2_THERECKONING), new Legacy2TheReckoning(), visual, fileName + "3" + fileType);
		//exec.replayGame(fileName,visual);
		//*/
	}
	
    /**
     * For running multiple games without visuals. This is useful to get a good idea of how well a controller plays
     * against a chosen opponent: the random nature of the game means that performance can vary from game to game. 
     * Running many games and looking at the average score (and standard deviation/error) helps to get a better
     * idea of how well the controller is likely to do in the competition.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController The Ghosts controller
     * @param trials The number of trials to be executed
     */
    public void runExperiment(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,int trials)
    {
    	double avgScore=0;
    	
    	Random rnd=new Random(0);
		Game game;
		
		for(int i=0;i<trials;i++)
		{
			game=new Game(rnd.nextLong());
			
			while(!game.gameOver())
			{
		        game.advanceGame(pacManController.getMove(game.copy(),System.currentTimeMillis()+DELAY),
		        		ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
			}
			
			avgScore+=game.getScore();
			System.out.println(i+"\t"+game.getScore());
		}
		
		System.out.println(avgScore/trials);
    }
	
	/**
	 * Run a game in asynchronous mode: the game waits until a move is returned. In order to slow thing down in case
	 * the controllers return very quickly, a time limit can be used. If fasted gameplay is required, this delay
	 * should be put as 0.
	 *
	 * @param pacManController The Pac-Man controller
	 * @param ghostController The Ghosts controller
	 * @param visual Indicates whether or not to use visuals
	 * @param delay The delay between time-steps
	 */
	public void runGame(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,boolean visual,int delay)
	{
		Game game=new Game(0);

		GameView gv=null;
		
		if(visual)
			gv=new GameView(game).showGame();
		
		while(!game.gameOver())
		{
	        game.advanceGame(pacManController.getMove(game.copy(),-1),ghostController.getMove(game.copy(),-1));
	        
	        try{Thread.sleep(delay);}catch(Exception e){}
	        
	        if(visual)
	        	gv.repaint();
		}
	}
	
	/**
     * Run the game with time limit (asynchronous mode). This is how it will be done in the competition. 
     * Can be played with and without visual display of game states.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController The Ghosts controller
	 * @param visual Indicates whether or not to use visuals
     */
    public void runGameTimed(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,boolean visual)
	{
		Game game=new Game(0);
		
		GameView gv=null;
		
		if(visual)
			gv=new GameView(game).showGame();
		
		if(pacManController instanceof HumanController)
			gv.getFrame().addKeyListener(((HumanController)pacManController).getKeyboardInput());
				
		new Thread(pacManController).start();
		new Thread(ghostController).start();
		
		while(!game.gameOver())
		{
			pacManController.update(game.copy(),System.currentTimeMillis()+DELAY);
			ghostController.update(game.copy(),System.currentTimeMillis()+DELAY);

			try
			{
				Thread.sleep(DELAY);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

	        game.advanceGame(pacManController.getMove(),ghostController.getMove());	   
	        
	        if(visual)
	        	gv.repaint();
		}
		
		pacManController.terminate();
		ghostController.terminate();
	}
	
    /**
     * Run the game in asynchronous mode but proceed as soon as both controllers replied. The time limit still applies so 
     * so the game will proceed after 40ms regardless of whether the controllers managed to calculate a turn.
     *     
     * @param pacManController The Pac-Man controller
     * @param ghostController The Ghosts controller
     * @param fixedTime Whether or not to wait until 40ms are up even if both controllers already responded
	 * @param visual Indicates whether or not to use visuals
     */
    public void runGameTimedSpeedOptimised(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,boolean fixedTime,boolean visual)
 	{
 		Game game=new Game(0);
 		
 		GameView gv=null;
 		
 		if(visual)
 			gv=new GameView(game).showGame();
 		
 		if(pacManController instanceof HumanController)
 			gv.getFrame().addKeyListener(((HumanController)pacManController).getKeyboardInput());
 				
 		new Thread(pacManController).start();
 		new Thread(ghostController).start();
 		
 		while(!game.gameOver())
 		{
 			pacManController.update(game.copy(),System.currentTimeMillis()+DELAY);
 			ghostController.update(game.copy(),System.currentTimeMillis()+DELAY);

 			try
			{
				int waited=DELAY/INTERVAL_WAIT;
				
				for(int j=0;j<DELAY/INTERVAL_WAIT;j++)
				{
					Thread.sleep(INTERVAL_WAIT);
					
					if(pacManController.hasComputed() && ghostController.hasComputed())
					{
						waited=j;
						break;
					}
				}
				
				if(fixedTime)
					Thread.sleep(((DELAY/INTERVAL_WAIT)-waited)*INTERVAL_WAIT);
				
				game.advanceGame(pacManController.getMove(),ghostController.getMove());	
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
 	        
 	        if(visual)
 	        	gv.repaint();
 		}
 		
 		pacManController.terminate();
 		ghostController.terminate();
 	}
    
	/**
	 * Run a game in asynchronous mode and recorded.
	 *
     * @param pacManController The Pac-Man controller
     * @param ghostController The Ghosts controller
     * @param visual Whether to run the game with visuals
	 * @param fileName The file name of the file that saves the replay
	 */
	public void runGameTimedRecorded(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,boolean visual,String fileName)
	{
		StringBuilder replay=new StringBuilder();
		
		Game game=new Game(0);
		
		GameView gv=null;
		
		if(visual)
		{
			gv=new GameView(game).showGame();
			
			if(pacManController instanceof HumanController)
				gv.getFrame().addKeyListener(((HumanController)pacManController).getKeyboardInput());
		}		
		
		new Thread(pacManController).start();
		new Thread(ghostController).start();
		
		while(!game.gameOver())
		{
			pacManController.update(game.copy(),System.currentTimeMillis()+DELAY);
			ghostController.update(game.copy(),System.currentTimeMillis()+DELAY);

			try
			{
				Thread.sleep(DELAY);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

	        game.advanceGame(pacManController.getMove(),ghostController.getMove());	        
	        
	        if(visual)
	        	gv.repaint();
	        
	        replay.append(game.getGameState()+"\n");
		}
		
		pacManController.terminate();
		ghostController.terminate();
		
		saveToFile(replay.toString(),fileName,false);
	}
	
	/**
	 * Replay a previously saved game.
	 *
	 * @param fileName The file name of the game to be played
	 * @param visual Indicates whether or not to use visuals
	 */
	public void replayGame(String fileName,boolean visual)
	{
		ArrayList<String> timeSteps=loadReplay(fileName);
		
		Game game=new Game(0);
		
		GameView gv=null;
		
		if(visual)
			gv=new GameView(game).showGame();
		
		for(int j=0;j<timeSteps.size();j++)
		{			
			game.setGameState(timeSteps.get(j));

			try
			{
				Thread.sleep(DELAY);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
	        if(visual)
	        	gv.repaint();
		}
	}
	
	//save file for replays
    public static void saveToFile(String data,String name,boolean append)
    {
        try 
        {
            FileOutputStream outS=new FileOutputStream(name,append);
            PrintWriter pw=new PrintWriter(outS);

            pw.println(data);
            pw.flush();
            outS.close();

        } 
        catch (IOException e)
        {
            System.out.println("Could not save data!");	
        }
    }  

    //load a replay
    private static ArrayList<String> loadReplay(String fileName)
	{
    	ArrayList<String> replay=new ArrayList<String>();
		
        try
        {         	
        	BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));	 
            String input=br.readLine();		
            
            while(input!=null)
            {
            	if(!input.equals(""))
            		replay.add(input);

            	input=br.readLine();	
            }
            
            br.close();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        
        return replay;
	}
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    private class Specimen {
    	public int[] genome;
    	public float average_score;
    	public boolean calculated;
    	public int trials;
    	
    	public Specimen() {
    		this(RandomGenome());
    	}
    	
    	public Specimen(int[] genome) {
    		this.genome = genome;
    		
    		this.average_score = -1;
    		this.calculated = false;
    		this.trials = 0;
    	}
    }
    
    public void runEvolution(Controller<EnumMap<GHOST,MOVE>> ghostController,
    		int population_size, int mutate_count, int mutate_percentage, int crossover_count, int new_count, int runs, int games_per_run
    ) {
    	LinkedList<Specimen> population = new LinkedList<Specimen>();
    	population = AddNew(population, population_size);
    	
    	int keep_count = population_size - mutate_count - crossover_count - new_count;
    	
    	for (int evolution_iteration = 0; evolution_iteration < runs; evolution_iteration++) {
    		System.out.print(evolution_iteration + ":\t");
    		
    		population = Evaluate(population, ghostController, games_per_run);
    		population = Sort(population);
    		
    		PrintTopPerformer(population);
    		
    		population = Keep(population, keep_count);
    		population = AddMutators(population, mutate_count, mutate_percentage);
    		population = AddCrossovers(population, crossover_count);
    		population = AddNew(population, new_count);
    		
    		population = EnsureSize(population, population_size);
    	}
    	
    	population = Evaluate(population, ghostController, games_per_run);
		population = Sort(population);
		
		PrintTopPerformer(population);
    }
    
    private class PacManTrial implements Runnable {
    	private Random rnd;
    	private Game game;
    	private JumoFSM pacManController;
    	private Controller<EnumMap<GHOST,MOVE>> ghostController;
    	
    	public PacManTrial(int[] genome, Controller<EnumMap<GHOST,MOVE>> ghostController) {
    		this.rnd = new Random();
			this.game = new Game(rnd.nextLong());
			
			this.pacManController = new JumoFSM(genome);
			this.ghostController = ghostController;
    	}
    	
		@Override
		public void run() {
			while(!game.gameOver())
			{
		        game.advanceGame(
		        		pacManController.getMove(game.copy(),System.currentTimeMillis()+DELAY),
		        		ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY)
		        );
			}
			
			AVERAGE_SCORE += game.getScore();
		}
    	
    }
    
    private float AVERAGE_SCORE;
    private float PREVIOUS_SCORE_SIGNIFICANCE = 0.1f;
    private int MAX_NUMBER_OF_THREADS = 10;
	private ExecutorService executor = Executors.newFixedThreadPool(MAX_NUMBER_OF_THREADS);
    
    private LinkedList<Specimen> Evaluate(LinkedList<Specimen> population, Controller<EnumMap<GHOST,MOVE>> ghostController, int games_per_run) {
    	for (Specimen s : population) {
    		
    		System.out.print(".");
    		
    		AVERAGE_SCORE = 0;
        	
    		int games_left = games_per_run;
    		
    		while (games_left > 0) {
    			int number_of_threads = Math.min(MAX_NUMBER_OF_THREADS, games_left);
    			
    			List<Callable<Object>> tasks = new ArrayList<Callable<Object>>(number_of_threads);
    			for(int i=0;i<number_of_threads;i++)
        		{
        			try {
						tasks.add(Executors.callable(new PacManTrial(s.genome, ghostController.getClass().newInstance())));
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
        		}
    			
    			try {
					executor.invokeAll(tasks);
					games_left -= number_of_threads;
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
    		}
    		
    		s.average_score = s.average_score * PREVIOUS_SCORE_SIGNIFICANCE + (AVERAGE_SCORE / games_per_run) * (1.0f - PREVIOUS_SCORE_SIGNIFICANCE);
    		s.calculated = true;
    		s.trials += 1;
    	}
    	
		return population;
	}

	private LinkedList<Specimen> EnsureSize(LinkedList<Specimen> population, int population_size) {
    	int diff = population_size - population.size();
    	
    	if (diff > 0) {
    		population = AddNew(population, diff);
    	} else {
    		population = Keep(population, population_size);
    	}
    	
		return population;
	}

	private void PrintTopPerformer(LinkedList<Specimen> population) {
    	Specimen top = population.peekFirst();
    	
		System.out.print(top.average_score + "\t (" + top.trials + ")\t { ");
		
		for (int g : top.genome) {
			System.out.print(g + ", ");
		}
		
		System.out.println("}");
	}

	private LinkedList<Specimen> AddNew(LinkedList<Specimen> population, int new_count) {
		for (int count = 0; count < new_count; count++) {
			population.add(new Specimen());
		}
    	
		return population;
	}

	private LinkedList<Specimen> AddCrossovers(LinkedList<Specimen> population, int crossover_count) {
		
		for (int i = 0; i < crossover_count; i++) {
			int[] genome1 = population.get(i * 2).genome;
			int[] genome2 = population.get(i * 2 + 1).genome;
			
			population.add(new Specimen(CrossoverGenomes(genome1, genome2)));
		}
		
		return population;
	}

	private LinkedList<Specimen> AddMutators(LinkedList<Specimen> population, int mutate_count, int mutation_percentage) {
		for (int i = 0; i < mutate_count; i++) {
			population.add(new Specimen(MutateGenome(population.get(i).genome, mutation_percentage)));
		}
		
		return population;
	}

	private static LinkedList<Specimen> Sort(LinkedList<Specimen> population) {
		Collections.sort(population, new Comparator<Specimen>() {
			@Override
			public int compare(Specimen arg0, Specimen arg1) {
				return Float.compare(arg1.average_score, arg0.average_score);
			}
			
		});
		
    	return population;
    }
    
    private static LinkedList<Specimen> Keep(LinkedList<Specimen> population, int count) {
    	while (population.size() > count) {
    		population.removeLast();
    	}
    	
    	return population;
    }
    
    private static int[] RandomGenome() {
    	Random rnd = new Random();
    	
    	int[] genome = new int[JumoFSM.GENOME_LENGTH];
    	for (int i = 0; i < genome.length; i++) {
    		genome[i] = rnd.nextInt(JumoFSM.MAX_GENOME_VALUE);
    	}
    	
    	return genome;
    }
    
    private static int[] MutateGenome(int[] old_genome, int mutation_percentage) {
    	Random rnd = new Random();
    	
    	int[] mutated_genome = new int[old_genome.length];
    	
    	for (int i = 0; i < mutated_genome.length; i++) {
    		// A number in the range [-mutation_percentage ; mutation_percentage]
        	int percentage_intrange = (rnd.nextInt(mutation_percentage * 2) - mutation_percentage);
        	
        	// Converted to a real percentage
        	float percentage = percentage_intrange / 100.0f;
        	
    		mutated_genome[i] = Math.round(old_genome[i] * (1.0f + percentage));
    	}
    	
    	return mutated_genome;
    }
    
    private static int[] CrossoverGenomes(int[] g1, int[] g2) {
    	int[] genome = new int[g1.length];
    	
    	for (int i = 0; i < genome.length; i++) {
    		if (i % 3 == 0) {
    			genome[i] = g1[i];
    		}
    		else if (i % 3 == 1) {
    			genome[i] = g2[i];
    		}
    		else {
    			genome[i] = Math.round((g1[i] + g2[i]) / 2.0f);
    		}
    	}
    	
    	return genome;
    }
}