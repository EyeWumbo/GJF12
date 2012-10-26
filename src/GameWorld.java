import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;
import org.lwjgl.input.Controller;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;

public class GameWorld
{

	public final int winsNeeded = 3;
	private List<Block> listOfBlocks;;
	private List<Platform> listOfPlatforms;;
	private List<Projectile> listOfProjectiles;
	private List<Character> listOfCharacters;
	private List<Item> listOfItems;
	private List<Item> itemsOnMap;
	private List<Map> listOfMaps;
	private Map map;

	private IceMap iceMap = new IceMap(this,"assets/Art/Background/bg_ice.jpg", "assets/SFX/music/Ice.wav");
	private LavaMap lavaMap = new LavaMap(this,"assets/Art/Background/bg_volcano.jpg", "assets/SFX/music/Volcano.wav");
	private SpaceMap spaceMap = new SpaceMap(this,"assets/Art/Background/bg_space.jpg", "assets/SFX/music/Space.wav");
	//ClockMap clockMap = new ClockMap(this,"assets/Art/Background/bg_space.jpg","assets/SFX/music/Ice.wav");
	private ControllerManager controllerManager;
	private Image background;

	public ArrayList<Sound> punchHit;
	public ArrayList<Sound> punchMiss;
	private ArrayList<Block> removeCrates;
	private ArrayList<Item> itemsToRemove;
	private ArrayList<Projectile> projectilesToBeRemoved;
	private int numberOfPlayers;
	private List<Character> listOfPlayers;
	private Sound BGM;

	public GameWorld(ControllerManager cm){
		controllerManager = cm;
		numberOfPlayers = 2;
	}

	public void init() throws IOException, SlickException 
	{	 
		listOfBlocks = new ArrayList<Block>();
		listOfPlatforms = new ArrayList<Platform>();
		listOfProjectiles = new ArrayList<Projectile>();
		listOfCharacters = new ArrayList<Character>();
		projectilesToBeRemoved =  new ArrayList<Projectile>();
		listOfItems = new ArrayList<Item>();
		itemsOnMap = new ArrayList<Item>();
		listOfMaps = new ArrayList<Map>();

		String earthFileLocation = "assets/Art/Transformations/icons/hammer.png";
		String fireFileLocation = "assets/Art/Transformations/icons/bow.png";
		String iceFileLocation = "assets/Art/Transformations/icons/shield.png";
		String lightningFileLocation = "assets/Art/Transformations/icons/dagger.png";
		String windFileLocation = "assets/Art/Transformations/icons/fan.png";

		listOfItems.add(new Earth(0, 0, earthFileLocation,0,0));		
		listOfItems.add(new Fire(0, 0, fireFileLocation,0,0));		
		listOfItems.add(new Ice(0, 0, iceFileLocation,0,0));
		listOfItems.add(new Lightning(0, 0, lightningFileLocation,0,0));
		listOfItems.add(new Wind(0, 0, windFileLocation,0,0));
		for(Item i : listOfItems){
			try {
				i.projectileImage = new Image(i.projectileImageLocation);
			} catch (SlickException e) {
				// TODO Auto-generated catch block
				Sys.alert("Something went wrong!", e.getMessage());
			}
		}
		
		iceMap.buildMap();
		map = iceMap;
		loadSounds();
		setBackgroundImage();
		loadChars();
	}

	public void loadSounds() throws SlickException
	{
		setBGM(map.getBGM());

		Sound punchHit1 = new Sound("assets/SFX/punch1Final.wav");
		Sound punchHit2 = new Sound("assets/SFX/punch2Final.wav");
		Sound punchHit3 = new Sound("assets/SFX/punch3Final.wav");
		Sound punchMiss1 = new Sound("assets/SFX/punchMiss1Final.wav");
		Sound punchMiss2 = new Sound("assets/SFX/punchMiss2Final.wav");
		Sound punchMiss3 = new Sound("assets/SFX/punchMiss3Final.wav");
		Sound bonesCrack = new Sound("assets/SFX/bonesCrackFinal.wav");
		Sound breakCrate = new Sound("assets/SFX/breakCrateFinal.wav");
		Sound clockBell = new Sound("assets/SFX/clockBellFinal.wav");
		Sound earthquake = new Sound("assets/SFX/earthquakesFinal.wav");
		Sound clockPulse = new Sound("assets/SFX/clockPulseFinal.wav");
		Sound rockHammer = new Sound("assets/SFX/rockHammerFinal.wav");
		Sound run = new Sound("assets/SFX/runFinal.wav");
		Sound shock = new Sound("assets/SFX/shockFinal.wav");
		Sound spikes = new Sound("assets/SFX/spikesFallFinal.wav");
		Sound ice = new Sound("assets/SFX/iceFinal.wav");
		Sound movingSteel = new Sound("assets/SFX/movingSteelFinal.wav");

		punchHit = new ArrayList<Sound>();
		punchMiss = new ArrayList<Sound>();

		punchHit.add(punchHit1);
		punchHit.add(punchHit2);
		punchHit.add(punchHit3);

		punchMiss.add(punchMiss1);
		punchMiss.add(punchMiss2);
		punchMiss.add(punchMiss3);



	}

	public void playRandomSound(ArrayList<Sound> soundList)
	{
		soundList.get((int)(soundList.size() * Math.random())).play();
	}

	public void loadChars() throws SlickException{
		List<Map.Location> characterSpawns = map.getCharacterSpawns();
		listOfPlayers = new ArrayList<Character>();
		for(int i = 0; i < getNumberOfPlayers() ; i++){
			int j = i+1;
			Map.Location loc = characterSpawns.get(i);
			Character c = new Character(loc.x, loc.y, "player" + j, this);
			c.setPlayerID(i);
			c.init();
			c.renderEnt(c.image, c.image.getWidth() / 3, c.image.getHeight());
			getListOfPlayers().add(c);
			listOfCharacters.add(c);
		}
	}

	public Map getMap()
	{
		return map;
	}

	public List<Block> getListOfBlocks()
	{
		return listOfBlocks;
	}

	public List<Projectile> getListOfProjectiles()
	{
		return listOfProjectiles;
	}

	public List<Character> getListOfCharacters()
	{
		return listOfCharacters;
	}


	public void checkForCollisions(GameContainer gc){
		ArrayList<Character> toBeRemoved = new ArrayList<Character>();
		//checking for character's collision with blocks
		for (Character c : listOfCharacters){
			Rectangle r = new Rectangle((int)(c.xCoord + c.xVelocity), (int)(c.yCoord + c.yVelocity), c.getHitBox().getWidth(), c.getHitBox().getHeight());
			for (Block b : listOfBlocks){
				if (r.intersects(b.getHitBox())){	
					if (b.getBlockType() == BlockType.Lethal){
						c.modifyHealth(c.getHP());
					}
					c.determineDirection();
					if (c.isMovingLeft && b.getBlockType() != BlockType.Passable){
						if(c.getX() >= b.getX() + b.getHitBox().getWidth())
							c.canMoveLeft = false;
						if (c.isMovingUp && b.getBlockType() != BlockType.Passable){
							if(c.getY() >= b.getY() + b.getHitBox().getHeight()){
								c.yVelocity = 0;
								c.jumpAvailable = false;
								c.canMoveUp = false;
							}
						}
						else if (c.isMovingDown){
							c.jumpAvailable = true;
							if(c.getHitBox().getY() + c.getHitBox().getHeight() < b.getHitBox().getY()){
								c.yVelocity = 0;
								c.canMoveDown = false;
							}
						}
					}
					else if(c.isMovingRight && b.getBlockType() != BlockType.Passable){
						//c.xVelocity = 0;
						if(c.getX() + c.getHitBox().getWidth() <= b.getX())
							c.canMoveRight = false;
						if (c.isMovingUp && b.getBlockType() != BlockType.Passable){
							if(c.getY() >= b.getHitBox().getY() + b.getHitBox().getHeight()){
								c.yVelocity = 0;
								c.jumpAvailable = false;
								c.canMoveUp = false;
							}
						}
						else if (c.isMovingDown){
							c.jumpAvailable = true;
							if(c.getHitBox().getY() + c.getHitBox().getHeight() < b.getHitBox().getY()){
								c.yVelocity = 0;
								c.canMoveDown = false;
							}
						}
					}
					else{
						if (c.isMovingUp && b.getBlockType() != BlockType.Passable){
							c.yVelocity = 0;
							c.jumpAvailable = false;
							c.canMoveUp = false;
						}
						else if (c.isMovingDown){
							c.jumpAvailable = true;
							if(c.getHitBox().getY() + c.getHitBox().getHeight() < b.getHitBox().getY()){
								c.yVelocity = 0;
								c.canMoveDown = false;
							}
						}
					}
				}
				if(r.getX() <= 0){
					c.canMoveLeft = false;
				}
				if(r.getX() + r.getWidth() >= gc.getWidth()){
					c.canMoveRight = false;
				}
				if(r.getY() <= 0){
					c.canMoveUp = false;
				}
				if(r.getY() > gc.getHeight()){
					c.modifyHealth(c.getHP());
				}

			}
		}

		for (Character c : toBeRemoved)
			listOfCharacters.remove(c);

		itemsToRemove = new ArrayList<Item>();

		projectilesToBeRemoved.clear();
		for (Projectile p : listOfProjectiles)
		{
			p.checkCollisions();
		}

		for (Character c : listOfCharacters)
		{
			for (Item i : itemsOnMap)
			{
				if (c.getHitBox().intersects(i.getHitBox()) && !c.hasItem)
				{
					c.pickUpItem(i);
					itemsToRemove.add(i);
				}
			}
		}

		removeCrates = new ArrayList<Block>();
		for (Block b: listOfBlocks)
		{
			if (b.getBlockType() == BlockType.Crate)
			{
				for (Projectile p : listOfProjectiles)
				{
					if (p.getHitBox().intersects(b.getHitBox()))
					{
						Item toBeAdded = chooseRandomItem();
						toBeAdded.setLocation(b.getX() + 30,b.getY());
						toBeAdded.setYSpawn(b.getY());
						itemsOnMap.add(toBeAdded);
						removeCrates.add(b);		
					}
				}	
				//				for (Character c : listOfCharacters)
				//				{
				//					if (c.isMovingRight)
				//					{
				//						Item toBeAdded = chooseRandomItem();
				//						toBeAdded.setLocation(b.getX() + 30,b.getY() + 30);
				//						itemsOnMap.add(toBeAdded);
				//						removeCrates.add(b);							
				//					}
				//				}
			}
		}

		for (Item i : itemsToRemove)
		{
			itemsOnMap.remove(i);	
		}

		for (Block b :removeCrates)
		{
			listOfBlocks.remove(b);
		}

		for (Projectile p : projectilesToBeRemoved)
			listOfProjectiles.remove(p);
	}

	public void update(GameContainer gc, int delta) throws SlickException, InterruptedException
	{
		for(int i = 0; i < getNumberOfPlayers(); i++){
			assignActionToPlayer(gc,i,delta);
		}
		for (Character c : listOfCharacters)
		{
			c.yVelocity += .15;
		}
		checkForCollisions(gc);





		//spawnItems();


		ArrayList<Character> charactersToBeRemoved = new ArrayList<Character>();
		for (Character c: listOfCharacters)
		{
			try{
				c.update(gc, delta);
				if(c.getHP() <= 0){
					charactersToBeRemoved.add(c);
				}
			}catch(Exception e){

			}			
		}
		for(Character c: charactersToBeRemoved){
			listOfCharacters.remove(c);
		}

		projectilesToBeRemoved.clear();
		for (Projectile p : listOfProjectiles)
		{
			p.update(gc, delta);	
		}
		for (Projectile p : projectilesToBeRemoved)
			listOfProjectiles.remove(p);

		for (Item i : itemsOnMap)
		{
			i.update(gc, delta);
		}
	}

	public void setNextRound() throws IOException, SlickException {
		
		listOfBlocks = new ArrayList<Block>();
		listOfPlatforms = new ArrayList<Platform>();
		listOfProjectiles = new ArrayList<Projectile>();
		itemsOnMap = new ArrayList<Item>();
		map = getNextMap();
		map.buildMap();

		for(int i = 0; i < getNumberOfPlayers(); i++){
			Character c = getListOfPlayers().get(i);
			c.reset();
			Map.Location loc = map.getCharacterSpawns().get(i);
			c.setLocation(loc.x * MapEntity.BLOCKSIZE, loc.y * MapEntity.BLOCKSIZE);
			c.setHitBox(c.xCoord, c.yCoord);
		}
		listOfCharacters = new ArrayList<Character>(getListOfPlayers());
		setBackgroundImage();
		getBGM().stop();
		setBGM(map.getBGM());
		getBGM().loop();
	}

	public void render(GameContainer gc, Graphics g) throws SlickException{
		getBackground().draw();
		for(Item i : itemsOnMap)
		{
			i.render(gc, g);
		}

		for(Block b: listOfBlocks){
			try{
				b.render(gc, g);

			}
			catch(NullPointerException ex){
				listOfBlocks.remove(b);
			}
		}
		for(Projectile p : listOfProjectiles){
			p.render(gc, g);

		}
		for(Character c : listOfCharacters){
			if(c.getHP() > 0){
				c.render(gc, g);
			}
		}
	}



	public Item chooseRandomItem()
	{
		return listOfItems.get((int)(listOfItems.size() * Math.random()));
	}

	//	public void spawnItems()
	//	{
	//		for (Block block : listOfBlocks)
	//		{
	//			if(block.getBlockType() == BlockType.Crate && Math.random() < .2)
	//			{
	//				Block toBeSpawned;
	//				toBeSpawned.setX(block.getX());
	//				toBeSpawned.setY(block.getY());
	//				itemsOnMap.add(toBeSpawned);
	//			}
	//		}
	//	}

	public void addBlock(Block block){
		listOfBlocks.add(block);
	}

	public void assignActionToPlayer(GameContainer gc, int characterIndex,int delta){
		Character c = getListOfPlayers().get(characterIndex);
		Input input = gc.getInput();
		if(c == null){
			return;
		}
		if(characterIndex == 0){
			if(input.isKeyDown(Input.KEY_A)){
				c.xVelocity = -3;
				c.canMoveLeft = true;
			}
			if(input.isKeyDown(Input.KEY_W)){
				if(c.jumpAvailable){
					c.yVelocity = -7;
					c.hasDX = false;
					c.jumpAvailable = false;
					c.canMoveUp = true;
				}
			}

			if(input.isKeyDown(Input.KEY_SPACE))
				c.attack();

			if(input.isKeyDown(Input.KEY_Q)){

				for (Item i : itemsOnMap)
				{
					if (c.getHitBox().intersects(i.getHitBox()))
					{
						c.dropItem();
						c.pickUpItem(i);
						itemsToRemove.add(i);
					}
				}
				for (Item i :itemsToRemove)
				{
					itemsOnMap.remove(i);
				}
			}
			if(input.isKeyDown(Input.KEY_D)){
				c.xVelocity = 3;
				c.canMoveRight = true;
			}
		}



		if(characterIndex == 1){
			if(input.isKeyDown(Input.KEY_J) || input.isKeyDown(Input.KEY_NUMPAD4)){ 
				c.xVelocity = -3;
				c.canMoveLeft = true;
			}
			if(input.isKeyDown(Input.KEY_I) || input.isKeyDown(Input.KEY_NUMPAD8)){ //KEY_NUMPAD8
				if(c.jumpAvailable){
					c.yVelocity = -7;
					c.hasDX = false;
					c.jumpAvailable = false;
					c.canMoveUp = true;
				}
			}
			if(input.isKeyDown(Input.KEY_O) || input.isKeyDown(Input.KEY_NUMPAD7)){//KEY_NUMPAD7
				for (Item i : itemsOnMap)
				{
					if (c.getHitBox().intersects(i.getHitBox()))
					{
						c.dropItem();
						c.pickUpItem(i);
						itemsToRemove.add(i);
					}
				}
				for (Item i :itemsToRemove)
				{
					itemsOnMap.remove(i);
				}
			}
			if(input.isKeyDown(Input.KEY_L) || input.isKeyDown(Input.KEY_NUMPAD6)){//NUMPAD6
				c.xVelocity = 3;
				c.canMoveRight = true;
			}
			if(input.isKeyDown(Input.KEY_U) || input.isKeyDown(Input.KEY_NUMPAD0)){//NUMPAD0
				c.attack();
			}
		}

		if(characterIndex == 2){
			if(input.isKeyDown(Input.KEY_LEFT)){
				c.xVelocity = -3;
				c.canMoveLeft = true;
			}
			if(input.isKeyDown(Input.KEY_UP)){
				if(c.jumpAvailable){
					c.yVelocity = -7;
					c.hasDX = false;
					c.jumpAvailable = false;
					c.canMoveUp = true;
				}
			}
			if(input.isKeyDown(Input.KEY_RIGHT)){
				c.xVelocity = 3;
				c.canMoveRight = true;
			}
		}

		if(characterIndex == 3){
			if(input.isKeyDown(Input.KEY_F)){
				c.xVelocity = -3;
				c.canMoveLeft = true;
			}
			if(input.isKeyDown(Input.KEY_T)){
				if(c.jumpAvailable){
					c.yVelocity = -7;
					c.hasDX = false;
					c.jumpAvailable = false;
					c.canMoveUp = true;
				}
			}
			if(input.isKeyDown(Input.KEY_H)){
				c.xVelocity = 3;
				c.canMoveRight = true;
			}
		}

		if(controllerManager != null){
			controllerManager.pollControllers();
			for(int i = 0; i < controllerManager.getControllerCount(); i++){
				Controller ctr = controllerManager.getController(i);

				if(ctr.getXAxisValue() < -0.75 && ctr.getYAxisValue() < 0.75){

					c.xVelocity = -1;
					c.canMoveLeft = true;
				}
				else if(ctr.getXAxisValue() > 0.75 && ctr.getYAxisValue() < 0.75){

					c.xVelocity = 3;
					c.canMoveRight = true;
				}
				if(ctr.isButtonPressed(Button.A.buttonID)){
					if(c.jumpAvailable){
						c.yVelocity = -5;
						c.hasDX = false;
						c.jumpAvailable = false;
						c.canMoveUp = true;
					}
				}
				if(ctr.isButtonPressed(Button.B.buttonID)){
					for (Item item : itemsOnMap)
					{
						if (c.getHitBox().intersects(item.getHitBox()))
						{
							c.dropItem();
							c.pickUpItem(item);
							itemsToRemove.add(item);
						}
					}
					for (Item item :itemsToRemove)
					{
						itemsOnMap.remove(item);
					}
				}
				if(ctr.isButtonPressed(Button.X.buttonID)){
					c.attack();
				}
			}
		}
		c.determineDirection();

	}

	private void setBackgroundImage(){
		background = map.getBackground();
	}

	public boolean checkIsRoundOver(){
		return listOfCharacters.size() <= 1;
	}

	private Map getNextMap() {
		if(map instanceof IceMap){
			return lavaMap;
		}
		else if(map instanceof LavaMap){
			return spaceMap;
		}
		else{
			return iceMap;
		}

	}

	public Image getBackground() {
		return background;
	}
	
	public void removeProjectile(Projectile projectileToRemove){
		projectilesToBeRemoved.add(projectileToRemove);
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public List<Character> getListOfPlayers() {
		return listOfPlayers;
	}

	public Sound getBGM() {
		return BGM;
	}

	public void setBGM(Sound BGM) {
		this.BGM = BGM;
	}
}

