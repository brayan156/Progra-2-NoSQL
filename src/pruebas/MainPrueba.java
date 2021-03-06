package pruebas;

import Listas.SimpleList_Comparable;
import Listas.SimpleNode_Comparable;
import javafx.application.Application;
import javafx.stage.Stage;


public class MainPrueba extends Application {
	
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception{
    	SimpleList_Comparable<Integer> list = new SimpleList_Comparable<>();
    	list.addLast(8);
    	list.addLast(4);
    	list.addLast(6);
    	list.addLast(7);
    	list.addLast(5);
    	list.addLast(0);
    	list.addLast(3);
    	list.addLast(2);
    	list.addLast(9);
    	list.addLast(1);
    	list.print();
    	list.selectionSort();
    	list.print();
    	
    }
}