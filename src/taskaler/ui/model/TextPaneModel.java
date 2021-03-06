package taskaler.ui.model;

import java.util.HashMap;

import taskaler.ui.controller.common;

/**
 * Model Associated with the TextPane
 * 
 * @author Cheah Kit Weng, A0059806W
 *
 */
//@author A0059806W
public class TextPaneModel implements IModel {
    // State Attributes
    public static final String TEXT_BODY_ATTRIBUTE = "TEXTBODY";
    public static final String TITLE_ATTRIBUTE = "TITLE";

    public String title;

    public String textBody;

    public TextPaneModel() {
        title = common.EMPTY_STRING;
        textBody = common.EMPTY_STRING;
    }

    @Override
    public HashMap<String, String> toHashMap() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put(VIEW_ATTRIBUTE, VIEW_TEXT_PANE);
        result.put(TITLE_ATTRIBUTE, title);
        result.put(TEXT_BODY_ATTRIBUTE, textBody);
        return result;
    }
}
