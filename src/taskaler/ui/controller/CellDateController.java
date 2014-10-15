/**
 * Package that contains all controllers used by the UI Component
 */
package taskaler.ui.controller;

import taskaler.ui.model.CellDateModel;
import taskaler.common.data.FXML_CONSTANTS;
import taskaler.common.enumerate.RECTANGLE_COLOR;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Class that acts as the controller for cellDate FXML. This class modifies a
 * single cell of the calendar
 * 
 * @author Cheah Kit Weng, A0059806W
 *
 */
public class CellDateController extends AnchorPane implements IController {

    // Current model associated with this controller
    private CellDateModel currentModel = null;

    // Special Constants
    private static final int MAX_NUMBER_OF_TASKS_FOR_DISPLAY = 9;
    private static final int MIN_NUMBER_OF_TASK_FOR_DISPLAY = 1;

    // Binded FXML Elements
    @FXML
    private Label lblDate;

    @FXML
    private Pane paneBody;

    @FXML
    private Label lblNumber;

    @FXML
    private Rectangle rectangleGrey;

    @FXML
    private Rectangle rectangleGreen;

    @FXML
    private Rectangle rectangleOrange;

    @FXML
    private Rectangle rectangleRed;

    /**
     * Overloaded constructor
     * 
     * @param date
     *            The date to set the cell to
     * @throws IOException
     *             Thrown when error met while reading FXML
     */
    public CellDateController(CellDateModel model) throws IOException {

        currentModel = model;

        initialize(FXML_CONSTANTS.FXML_CELL_DATE);
        update();
    }

    @Override
    public void initialize(String FXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    public void setTitle(String title) {
        lblDate.setText(title);
    }

    @Override
    public void update() {
        setTitle(currentModel.currentDate + FXML_CONSTANTS.EMPTY_STRING);
        int totalNumberOfTasks = currentModel.currentNumberOfEvents;
        if(totalNumberOfTasks > 0){
            setNumberOfTasks(totalNumberOfTasks);
            setBodyVisible(true);
        }else{
            setBodyVisible(false);
        }
    }

    /**
     * Method to make the body of the cell visible
     * 
     * @param isVisible
     *            The boolean to determine if the body is visible
     */
    private void setBodyVisible(boolean isVisible) {
        paneBody.setVisible(isVisible);
    }

    /**
     * Method to set the number of tasks
     * 
     * @param totalNumberOfTasks
     *            The total number of tasks
     */
    private void setNumberOfTasks(int totalNumberOfTasks) {
        if (totalNumberOfTasks < MIN_NUMBER_OF_TASK_FOR_DISPLAY) {
            setBodyVisible(false);
            return;
        }
        if (totalNumberOfTasks > MAX_NUMBER_OF_TASKS_FOR_DISPLAY) {
            lblNumber.setText(MAX_NUMBER_OF_TASKS_FOR_DISPLAY + FXML_CONSTANTS.PLUS_STRING);
        } else {
            lblNumber.setText(totalNumberOfTasks + FXML_CONSTANTS.EMPTY_STRING);
        }
    }

    /**
     * Method to reset visibility of all circles to false
     * 
     */
    private void resetCircleVisibility() {
        rectangleGrey.setVisible(false);
        rectangleGreen.setVisible(false);
        rectangleOrange.setVisible(false);
        rectangleRed.setVisible(false);
    }

    /**
     * Method to set the visibility of each circle
     * 
     * @param grey
     *            Boolean for the visibility of the grey circle
     * @param green
     *            Boolean for the visibility of the green circle
     * @param orange
     *            Boolean for the visibility of the orange circle
     * @param red
     *            Boolean for the visibility of the red circle
     */
    private void setCircleVisible(boolean grey, boolean green, boolean orange,
            boolean red) {
        resetCircleVisibility();
        setCircleVisible(RECTANGLE_COLOR.GREY, grey);
        setCircleVisible(RECTANGLE_COLOR.GREEN, green);
        setCircleVisible(RECTANGLE_COLOR.ORANGE, orange);
        setCircleVisible(RECTANGLE_COLOR.RED, red);
    }

    /**
     * Private method to set the visibility of a circle
     * 
     * @param color
     *            The color of the circle to be set
     * @param isVisible
     *            The boolean to determine if circle is visible
     */
    private void setCircleVisible(RECTANGLE_COLOR color, boolean isVisible) {
        switch (color) {
        case GREY:
            rectangleGrey.setVisible(isVisible);
        case GREEN:
            rectangleGreen.setVisible(isVisible);
        case ORANGE:
            rectangleOrange.setVisible(isVisible);
        case RED:
            rectangleRed.setVisible(isVisible);
        }
    }

}
