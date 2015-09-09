
import g4p_controls.*;
import processing.serial.*;
import cc.arduino.*;

Arduino arduino;

Serial arduinoPort;
Serial myPort;

PImage main_menu, gameplay, game_over;

//Duration of the game
int duration;

//Holds the mode Trivino is currently on
int mode;

//All of Trivino's modes
final int MAIN_MENU = 0, GAMEPLAY = 1, GAME_OVER = 2;

//Holds the game object
Game game;

//The arraylist that holds all the questions of Trivino
ArrayList < Question > questions = new ArrayList < Question > ();
final int A = 0, B = 1, C = 2, D = 3;

public void setup() {
  size(800, 480, JAVA2D);
  createGUI();
  main_menu = loadImage("mainmenu.png");
  gameplay = loadImage("gameplay.png");
  game_over = loadImage("gameover.png");
  myPort = new Serial(this, Serial.list()[0], 9600);
  mode = MAIN_MENU;
  duration = 3;
  loadQuestions();
}

//Game loop
public void draw() {
  drawBackground();
  drawMode();
  handleInput(getArduinoInput());
}

//Draws the background
public void drawBackground() {
//  reset_fields();
  if (mode == MAIN_MENU) {
    background(main_menu);
  } else if (mode == GAMEPLAY) {
    background(gameplay);
  } else if (mode == GAME_OVER) {
    background(game_over);
  }
}

//Draw the modes
public void drawMode() {
  if (mode == MAIN_MENU) {

  } else if (mode == GAMEPLAY) {
    game.draw();
  } else if (mode == GAME_OVER) {
      
  }
}

void keyPressed() {
    handleInput(key + "");
    key = 0;
}

//Handle inputs
public void handleInput(String input) {
  if (input == null) {
      return;
  }
  println(input);
  if (mode == MAIN_MENU) {
    handleMainMenuInput(input);
  } else if (mode == GAMEPLAY) {
    game.handleInput(input);
  } else if (mode == GAME_OVER) {
    handleGameOverInput(input);
  }
}

public String getArduinoInput() {
  String input = null;
  if (myPort.available() > 0) {
    input = myPort.readStringUntil('\n');
    myPort.clear();
  }
  return input;
}

public void handleMainMenuInput(String input) {
  if (input.contains("a") && (duration - 1 > 0)) {
    TTL.setText(Integer.toString(--duration));
  } else if (input.contains("b")) {
    TTL.setText(Integer.toString(++duration));
  } else if (input.contains("c")) { //Start game
      game = new Game(duration);
      mode = GAMEPLAY;
  }
}

public void handleGameOverInput(String input){
    if (input.contains("c")) { //Return to main menu
    mode = MAIN_MENU;
  }
}

class Game {

  int start; //Time the game started

  int points; //The number of points the player has accumulated

  int duration; //How long the game is suppose to last in seconds

  Question question; //Holds current question id

  Game(int dur) {
    duration = dur * 60 * 1000;
    start = millis();
    points = 0;
    createGUI2(); //Creates labels
    newQuestion();
  }

  //Returns the number of points the player has accumulated
  int getPoints() {
    return points;
  }

  //Rewards the player with 3 points for a correct answer
  void correct() {
    points += 3;
    //Sound and lights
    myPort.write('G');
  }

  //Penalizes the player for an incorrect answer
  void incorrect() {
    points -= 1;
    if (points < 0) { //We don't want negative scores
      points = 0;
    }
    myPort.write('R');
    //Sound and lights
  }

  //Handles input from Arduino
  void handleInput(String input) {
        int selected = getSelection(input);
    if (selected != -1){ //If it's a valid choice
        if (question.isCorrect(selected)){
            //Answer is correct
            correct();
        } else { 
            //Answer is incorrect
            incorrect();
        }
        newQuestion();
    }
  }

    //Gets the answer choice the user chose
    int getSelection(String input){
        if (input.contains("a")) {
            return A;
    } else if (input.contains("b")) {
      return B;
    } else if (input.contains("c")) {
      return C;
    } else if (input.contains("d")) {
      return D;
    } 
    return -1;
    }

  //Changes to a new question
  void newQuestion() {
    int id = (int) random(questions.size());
    question = questions.get(id);
    prompt.setText(question.getQuestion());
    ansA.setText("A) " + question.getA());
    ansB.setText("B) " + question.getB());
    ansC.setText("C) " + question.getC());
    ansD.setText("D) " + question.getD());
    setImage(question.getImage());
  }

  void draw() {
      pointsField.setText("Points: " + Integer.toString(points));
    if (duration - millis() - start <= 0) {
      mode = GAME_OVER;
      reset_fields();
    } else {
      time_left.setText("Time left: " + getTimeString());
    }
  }

  String getTimeString() {
    int seconds = (int)((duration - (millis() - start)) / 1000) % 60;
    int minutes = (int)(((duration - (millis() - start)) / (1000 * 60)) % 60);
    return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
  }
}

class Question {

    String image;

  String question; //The question prompt

  String A, B, C, D; //The 4 answer choices

  int answer; //The answer to the question where 0 = A, 1 = B, 2 = C, 3 = D

  Question(String img, String quest, String a, String b, String c, String d, int ans) {
      image = img;
    question = quest;
    A = a;
    B = b;
    C = c;
    D = d;
    answer = ans;
  }
  //Returns the question prompt
  String getQuestion() {
    return question;
  }

  //Returns answer choice A
  String getA() {
    return A;
  }

  //Returns answer choice B
  String getB() {
    return B;
  }

  //Returns answer choice C
  String getC() {
    return C;
  }

  //Returns answer choice D
  String getD() {
    return D;
  }
  
  String getImage(){
      return image;
  }

  //True if the player chose correctly, false otherwise
  boolean isCorrect(int choice) {
    return choice == answer;
  }
}

//Some questions from
//http://www.studyzone.org/
//http://www.p12.nysed.gov/
//http://www.education.ne.gov/

void loadQuestions() {
    //5 GEOGRAPHY QUESTIONS
  questions.add(new Question("geo1.png", "One of seven very large areas that make up all of the earth's land is called a", "Country", "Continent", "City", "Town", B));
  questions.add(new Question("geo2.png", "The half of the earth that is north of the equator is the", "Latitude Hemisphere", "Southern Hemisphere", "Northern Hemisphere", "North Pole", C));
  questions.add(new Question("geo3.png", "Crops can be grown near rivers because the land is usually", "Rocky", "Dry", "Fertile", "Poor", B));
  questions.add(new Question("geo4.png", "If you lived in a desert, which of the following would you be least likely to see?", "Octupus", "Cactus", "Camel", "Sand", A));
  questions.add(new Question("geo5.png", "Which is not a body of water?", "River", "Ocean", "Sea", "Valley", A));
  
  //5 MATH QUESTIONS
  questions.add(new Question("us1.png", "Who was America's first president?", "George Washington", "Thomas Jefferson", "Barack Obama", "Benjamin Franklin", A));
  questions.add(new Question("us2.png", "How many original colonies were there?", "18", "50", "13", "10", C));
  questions.add(new Question("us3.png", "What year was the Declaration of Independence signed?", "1790", "1776", "1782", "1767", B));
  questions.add(new Question("us4.png", "No ________ without representation?", "Stamps", "War", "Tea", "Taxation", D));
  questions.add(new Question("us5.png", "What city was America's first capital?", "Washington DC", "Boston", "New York City", "Philadelphia", C));

  //5 SCIENCE QUESTIONS
  questions.add(new Question("sci1.png", "Which unit is used to measure how warm or cool the air is?", "Grams", "Kilometers", "Degress Celsius", "Cubie Centimeters", C));
  questions.add(new Question("sci2.png", "Which color fur will best protect a rabbit from a hawk in a snowy field?", "Brown", "Gray", "White", "Black", C));
  questions.add(new Question("sci3.png", "When water turns into a gas from a liquid, this is called", "Evaporation", "Condensation", "Precipitation", "Freezing", A));
  questions.add(new Question("sci4.png", "A toaster changes electrical energy to", "Solar energy", "Heat energy", "Sound energy", "Magnetic energy", B));
  questions.add(new Question("sci5.png", "The sun rises from the ", "North", "East", "South", "West", A));
  
  //5 MATH QUESTIONS
  questions.add(new Question("math1.png", "If Bob had 14 apples but loses 3 of them, how many does he have left?", "11", "14", "3", "10", A));
  questions.add(new Question("math2.png", "What is the area of a 10 foot by 15 foot table?", "10 feet sq", "150 feet sq", "200 feet sq", "250 feet sq", B));
  questions.add(new Question("math3.png", "What is the degrees of a right angle?", "10", "45", "90", "180", C));
  questions.add(new Question("math4.png", "If you cut a 22 inch string in half, how long is each piece?", "21", "5", "10", "11", D));
  questions.add(new Question("math5.png", "How many sides does a square have? ", "4", "3", "5", "6", A));
  
  //5 ENGLISH QUESTIONS
  //questions.add(new Question("eng1.png", "Which word is an adjective?", "Blue", "Cat", "Building", "Crayon", A));
  //questions.add(new Question("eng2.png", "A comparison using like or as is called a", "Metaphor", "Simile", "Verb", "Noun" B));
  //questions.add(new Question("eng3.png", "Which word is not a noun?", "Happy", "Meal", "Dog", "NYU", A));
  //questions.add(new Question("eng4.png", "Giving objects human characteristics is called", "Humanification", "Objectification", "Foreshadowing", "Personification", D));
  //questions.add(new Question("eng5.png", "A word that conveys an action is called a", "Plot", "Noun", "Verb", "Story", C));
}
