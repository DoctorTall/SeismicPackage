///
/// AppGUIController.java
///
///     Date       Author   Alterations
///----------------------------------------------------------------------------
/// 11. 07-22-2004 SMCook   Modified so that certain types of controls do not
///                          take setEditable literally.  This is to allow
///                          cut and paste to be usable from grayed-out
///                          process screens.  Involved a change to class
///                          ButtonPressActionHandler also.
/// 10. 09-05-2002 SMCook   Removed "Exception during tip update null" message.
///  9. 08-23-2002 SMCook   Added variables _newestJFrame and _firstJFrame to
///                          allow WindowControl to get sensible parents for
///                          JDialogs.
///                         Removed System.gc() command again - it was causing
///                          very sluggish paints even though it was being
///                          called after setVisible().
///  8. 08-20-2002 SMCook   Reinstated System.gc() command, but in a different
///                          location.
///                         Added logic to prevent UTILS button from having the
///                          focus at startup.
///  7. 08-02-2002 SMCook   Commented out call to logFreeMemory() since memory
///                          leak has long since been fixed.
///                         Made most lines of code adhere to an 80-char limit.
///  6. 09-18-2001 SMCook   To fix disappearing help tip when using arrows,
///                          eliminated _helpPanel variable, replacing with
///                          direct calls to setHelpPanel() and getHelpPanel().
///                          This eliminated redundancy in storing reference to
///                          the JPanel.  Also changed repaint to include only
///                          the help label (previously called repaint for
///                          entire window, potentially slower).
///
 
package com.conoco.cfe.client.gui.controller;

import com.conoco.cfe.client.ClientConstants;
  
import com.conoco.cfe.client.application.Console;

import com.conoco.cfe.client.gui.AppGUIBuilder;
import com.conoco.cfe.client.gui.AppGUIDecoder;
import com.conoco.cfe.client.gui.XMLHelper;

import com.conoco.cfe.client.gui.builder.HelpSection;

import com.conoco.cfe.client.gui.controls.GUIControl;
import com.conoco.cfe.client.gui.controls.GUIControlListener;
import com.conoco.cfe.client.gui.controls.GUIControlEvent;
import com.conoco.cfe.client.gui.controls.ArrayGUIControl;
import com.conoco.cfe.client.gui.controls.ArraySetGUIControl;
import com.conoco.cfe.client.gui.controls.ArraySetControl;
import com.conoco.cfe.client.gui.controls.MenuBarControl;
import com.conoco.cfe.client.gui.controls.FieldGUIControl;
import com.conoco.cfe.client.gui.controls.HelpWindowControl;
import com.conoco.cfe.client.gui.controls.IndexedArrayControl;
import com.conoco.cfe.client.gui.controls.TabPaneControl;
import com.conoco.cfe.client.gui.controls.TextFieldControl;
import com.conoco.cfe.client.gui.controls.ViewRangeArrayControl;
import com.conoco.cfe.client.gui.controls.WindowControl;

import com.conoco.cfe.client.gui.controller.actions.XmlActionHandler;
import com.conoco.cfe.client.gui.controller.actions.ErrorActionHandler;
import com.conoco.cfe.client.gui.controller.actions.InfoActionHandler;
import com.conoco.cfe.client.gui.controller.actions.InsertElementsActionHandler;
import com.conoco.cfe.client.gui.controller.actions.JumpArrayActionHandler;
import com.conoco.cfe.client.gui.controller.actions.JumpArrayElementActionHandler;
import com.conoco.cfe.client.gui.controller.actions.JumpArraySetActionHandler;
import com.conoco.cfe.client.gui.controller.actions.JumpArraySetRowActionHandler;
import com.conoco.cfe.client.gui.controller.actions.JumpFieldActionHandler;
import com.conoco.cfe.client.gui.controller.actions.JumpScreenActionHandler;
import com.conoco.cfe.client.gui.controller.actions.ModifyElementsActionHandler;
import com.conoco.cfe.client.gui.controller.actions.ModifyFieldActionHandler;
import com.conoco.cfe.client.gui.controller.actions.ModifyProcessActionHandler;
import com.conoco.cfe.client.gui.controller.actions.OptionsArrayActionHandler;
import com.conoco.cfe.client.gui.controller.actions.OptionsFieldActionHandler;
import com.conoco.cfe.client.gui.controller.actions.RemoveElementsActionHandler;
import com.conoco.cfe.client.gui.controller.actions.SetWindowTitleActionHandler;
import com.conoco.cfe.client.gui.controller.actions.SensitiveArrayActionHandler;
import com.conoco.cfe.client.gui.controller.actions.SensitiveArraySetActionHandler;
import com.conoco.cfe.client.gui.controller.actions.SensitiveFieldActionHandler;
import com.conoco.cfe.client.gui.controller.actions.SensitiveScreenActionHandler;
import com.conoco.cfe.client.gui.controller.actions.SetArrayBackgroundColorActionHandler;
import com.conoco.cfe.client.gui.controller.actions.ClearSelectionActionHandler;
import com.conoco.cfe.client.gui.controller.actions.WarningActionHandler;
import com.conoco.cfe.client.gui.controller.actions.ReplaceElementsActionHandler;
import com.conoco.cfe.client.gui.controller.actions.ClearElementsActionHandler;
import com.conoco.cfe.client.gui.controller.actions.SelectRadioButtonActionHandler;
import com.conoco.cfe.client.gui.controller.actions.BeepActionHandler;
import com.conoco.cfe.client.gui.controller.actions.JumpWindowActionHandler;
import com.conoco.cfe.client.gui.controller.actions.VisibleActionHandler;
import com.conoco.cfe.client.gui.controller.actions.ShowHelpActionHandler;
import com.conoco.cfe.client.gui.controller.actions.ClientVersionHandler;
import com.conoco.cfe.client.gui.controller.actions.SetMnemonicActionHandler;

import com.conoco.cfe.client.gui.messaging.AppGUIControlListener;
import com.conoco.cfe.client.gui.messaging.ButtonPressActionHandler;
import com.conoco.cfe.client.gui.messaging.GUIMessageHandlerAdapter;

import com.conoco.cfe.client.messaging.AppCommController;
import com.conoco.cfe.client.messaging.CommController;
import com.conoco.cfe.client.messaging.Preferences;

import com.conoco.cfe.utils.EventQueue;
import com.conoco.cfe.utils.ArrayList;

import com.conoco.xml.StringArray;

import java.awt.Color;
import java.awt.Container;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Toolkit;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import org.w3c.dom.Node;

/**
 * A class that implements functionality of a client's GUI controller. 
 * The client's state is changed by the comms controller (via the GUI controller) 
 * in response to the messages coming from the server. The GUI controller also
 * sends requests to the comms controller in response to
 * the update notifications received from the GUI controls.
 *
 * @see com.conoco.cfe.client.messaging.AppCommController
 * @see com.conoco.cfe.client.gui.controls
 */
public class AppGUIController implements GUIController {
  /**
   * Declares a variable for the communications controller.
   * 
   * @serial
   */
  protected CommController _commController;  
        
  /**
   * Declares a variable for the hashtable of action handlers
   * that handle the replies coming from the server.
   * 
   * @serial
   */
  protected Hashtable _replyActionHandlers;
  
  /**
   * Declares a variable for the GUI control listener.
   * 
   * @serial
   */
  protected AppGUIControlListener _guiControlListener;
      
  /**
   * Declares a variable for the help section.
   * 
   * @serial
   */
  protected HelpSection _currentHelpSection;
 
  /**
   * Declares a variable for the window which is under
   * focus.
   * 
   * @serial
   */
  protected WindowControl _windowUnderFocus;      

  /**
   *
   */
  private final Color veryLightGray = new Color(232, 232, 232);
 
  /**
   * References to the first and newest JFrames created.  Potentially
   * useful for other classes, e.g. creating a modal JDialog with a
   * reasonably sensible parent.
   * 
   * @serial
   */
  public static JFrame _firstJFrame;
  public static JFrame _newestJFrame;

  /**
   * Constructs a new GUI controller object.
   */
  public AppGUIController() {
    _replyActionHandlers = new Hashtable();
    createActionHandlers();
  }
  
  /**
   * Constructs a new GUI controller object with the specified
   * comm controller.
   * 
   * @param cc   the comms controller that will be invoked for 
   *         sending messages to the server
   */
  public AppGUIController(CommController cc) {
    this();
    setCommController(cc);
  }
  
  /**
   * Sets the comms controller.
   * 
   * @param cc   the comms controller that will be invoked 
   *         for sending messages to the server
   */
  public void setCommController(CommController cc) {
    _commController = cc;
    Hashtable handlers = AppGUIControlListener.getActionHandlers();
    Enumeration e = handlers.elements();
    while (e.hasMoreElements()) {
      ((GUIMessageHandlerAdapter) e.nextElement()).setCommController(
        cc);
    }
  }
    
  /**
   * Returns the top level window associated with a window id.
   * 
   * @param windowId the id
   * @return the window control associated with a window id
   */
//  private static WindowControl getTopLevelWindow(int windowId) {
//SMCook made this function public
  public static WindowControl getTopLevelWindow(int windowId) {
    Hashtable h = GUIControllerHelper.getWindowIdToWindow();
    return (WindowControl) h.get(new Integer(windowId));
  }
  
  /**
   * Returns the window id for the specified window control.
   * 
   * @param wc the window control whose window id is desired
   * @return the window id of the window
   */
  private static int getWindowIdForWindow(WindowControl wc) {
    Hashtable h = GUIControllerHelper.getWindowIdToWindow();
    Enumeration keys = h.keys();    
    while ( keys.hasMoreElements()) {
      Object o = keys.nextElement();
      if ( wc == h.get(o) ) {
        return ((Integer) o).intValue();  
      }
    }  
    return ClientConstants.UNDEFINED_WINDOW_ID;        
  }

  
  /**
   * Returns the comms controller.
   * 
   * @return the comms controller 
   */
  public CommController getCommController() {
    return _commController;
  }
  
  /**
   * Creates the action handlers that will handle the responses
   * coming from the server. These action handlers are invoked 
   * by the message decoder.
   */
  protected void createActionHandlers() {
    ActionHandlerHelper helper = new ActionHandlerHelper();
    _replyActionHandlers.put("SETGUI",                 new SetGUIActionHandler(false));
    _replyActionHandlers.put("TERMINATEAPP",           new TerminateAppActionHandler());
    _replyActionHandlers.put("CLOSE",                  new CloseActionHandler());
    _replyActionHandlers.put("ERROR",                  new ErrorActionHandler(helper));
    _replyActionHandlers.put("INFO",                   new InfoActionHandler(helper));
    _replyActionHandlers.put("INSERTELEMENTS",         new InsertElementsActionHandler(helper));
    _replyActionHandlers.put("JUMPARRAY",              new JumpArrayActionHandler(helper));
    _replyActionHandlers.put("JUMPARRAYELEMENT",       new JumpArrayElementActionHandler(helper));
    _replyActionHandlers.put("JUMPARRAYSET",           new JumpArraySetActionHandler(helper));
    _replyActionHandlers.put("JUMPARRAYSETROW",        new JumpArraySetRowActionHandler(helper));
    _replyActionHandlers.put("JUMPFIELD",              new JumpFieldActionHandler(helper));
    _replyActionHandlers.put("JUMPSCREEN",             new JumpScreenActionHandler(helper));
    _replyActionHandlers.put("MODIFYELEMENTS",         new ModifyElementsActionHandler(helper));
    _replyActionHandlers.put("MODIFYFIELD",            new ModifyFieldActionHandler(helper));
    _replyActionHandlers.put("MODIFYPROCESS",          new ModifyProcessActionHandler(helper));
    _replyActionHandlers.put("OPTIONSARRAY",           new OptionsArrayActionHandler(helper));
    _replyActionHandlers.put("OPTIONSFIELD",           new OptionsFieldActionHandler(helper));
    _replyActionHandlers.put("REMOVEELEMENTS",         new RemoveElementsActionHandler(helper));
    _replyActionHandlers.put("SENSITIVEARRAY",         new SensitiveArrayActionHandler(helper));
    _replyActionHandlers.put("SENSITIVEARRAYSET",      new SensitiveArraySetActionHandler(helper));
    _replyActionHandlers.put("SENSITIVEFIELD",         new SensitiveFieldActionHandler(helper));
    _replyActionHandlers.put("SENSITIVESCREEN",        new SensitiveScreenActionHandler(helper));
    _replyActionHandlers.put("SETARRAYBACKGROUNDCOLOR",new SetArrayBackgroundColorActionHandler(helper));
    _replyActionHandlers.put("CLEARSELECTION",         new ClearSelectionActionHandler(helper));
    _replyActionHandlers.put("WARNING",                new WarningActionHandler(helper));
    _replyActionHandlers.put("REPLACEELEMENTS",        new ReplaceElementsActionHandler(helper));
    _replyActionHandlers.put("CLEARELEMENTS",          new ClearElementsActionHandler(helper));
    _replyActionHandlers.put("SETWINDOWTITLE",         new SetWindowTitleActionHandler(helper));
    _replyActionHandlers.put("REPLACEGUI",             new SetGUIActionHandler(true));
    _replyActionHandlers.put("SELECTRADIOBUTTON",      new SelectRadioButtonActionHandler(helper));
    _replyActionHandlers.put("BEEP",                   new BeepActionHandler(helper));
    _replyActionHandlers.put("JUMPWINDOW",             new JumpWindowActionHandler(helper));
    _replyActionHandlers.put("VISIBLE",                new VisibleActionHandler(helper));
    _replyActionHandlers.put("SHOWHELP",               new ShowHelpActionHandler(helper));    
    _replyActionHandlers.put("CLIENTVERSION",          new ClientVersionHandler(helper));
    _replyActionHandlers.put("SETMNEMONIC",            new SetMnemonicActionHandler(helper));
  }
    
  /**
   * Returns the action handlers that handle the reply messages
   * coming from the server. The action handlers interact directly
   * with the controls to manipulate the client state.
   * 
   * @return the action handlers as a <code>java.util.Hashtable</code>
   */
  public Hashtable getReplyActionHandlers() {
    return _replyActionHandlers;
  }
    
  /**
   * Inner class that handles the "SetGUI" message.
   */
  private class SetGUIActionHandler implements XmlActionHandler {
    /**
     * Variable for the window id
     */
    private int _pid;
    
    /**
     * Boolean variable that is set if all the GUI is to be
     * non-sensitive and non-editable
     */
    private boolean _readOnly;
    
    /**
     * A boolean variable that is set if the Set GUI is being 
     * invoked to replace the GUI in an existing window
     */
    private boolean _replace;

    /**
     * Variable for the old window control whose GUI needs to 
     * be replaced with a new one
     */
    private WindowControl _originalWindow;
    
    /**
     * Constructs a new action handler.
     * 
     * @param replace   a boolean that is set to true if 
     *           an existing GUI is to be replaced
     */
    public SetGUIActionHandler(boolean replace) {
      _replace = replace;  
    }
    
    /**
     * Checks to see if a window with the specified pid already 
     * exists or not.
     * 
     * @return   <code>true</code> if window already exists; 
     *       <code>false</code> otherwise
     */
    private boolean checkWindowId(int pid) {
      Hashtable h = GUIControllerHelper.getWindowIdToWindow();
      if ( h.get(new Integer(pid)) != null ) {
        return true;
      } 
      else {
        return false;
      }
    }
    
    /**
     * This is invoked by the message decoder when it comes 
     * across a "SetGUI" or "ReplaceGUI" node.
     *
     * @param n the document node to be processed
     */    
    public void performAction(Node n) {
      String fileName = null;

      // return if it is "SetGUI" and window with specified pid already exists
      _pid = XMLHelper.getIntAttributeValue(n, "windowId");

      if ( (!_replace && checkWindowId(_pid)) ) {  
        return;  
      }

      _readOnly = XMLHelper.getBooleanAttributeValue(n, "keyword");
      
      if (!Preferences.getXMLDocumentPath().startsWith("file")) {
        fileName = "http://" + 
          Preferences.getServerName() +
            Preferences.getXMLDocumentPath() + 
              XMLHelper.getStringAttributeValue(n, "value");
      }
      else {
        fileName = Preferences.getXMLDocumentPath() +
                     XMLHelper.getStringAttributeValue(n, "value");
      }

      _guiControlListener = new AppGUIControlListener();
      _guiControlListener.setWindowId(_pid);
      
      WindowControl wc = setUpGUI(fileName);

      sendArrayNamesMessage();
 
      //Console.logFreeMemory("AppGUIController:");   //SMCook

      // if it is a "SetGUI" message      
      if ( !_replace ) {

        if ( wc != null ) {
          Window w = (Window) wc.getComponent();
          if ( wc.isDialog() ) {
            if ( ((JDialog) w).isModal() ) {
              MyThread t = new MyThread(wc);
              t.start();
            } 
            else {
              wc.setVisible(true);  
            }
          } 
          else {
            wc.setVisible(true);

            _newestJFrame = (JFrame)wc.getComponent();

            //SMCook - logic prevents UTILS button from getting initial focus
            if(_firstJFrame == null) {
              Container c = _newestJFrame.getRootPane();
              int ncomps = c.getComponentCount();
              c.getComponent(ncomps - 1).requestFocus();
              _firstJFrame = _newestJFrame;
            }
          }
        } 
        else {
          Console.logMessage("AppGUIController: Top Level window is null");  
        }
      } 
      
      // if it is a "ReplaceGUI"
      else {

        Hashtable h = GUIControllerHelper.getWindowIdToWindow();
        _originalWindow = (WindowControl) h.get(new Integer(_pid));

        _currentHelpSection = (HelpSection) 
        GUIControllerHelper.getWindowIdToHelpSection().get(new Integer(_pid));

        Container oc = _originalWindow.getContentPane();
        JRootPane rp = null;

        if ( _originalWindow.isDialog() ) {      
          rp = ((JDialog) _originalWindow.getComponent()).getRootPane();
        } 
        else {
          rp = ((JFrame) _originalWindow.getComponent()).getRootPane();
        }

        JLayeredPane lp = rp.getLayeredPane();

        if ( rp.getJMenuBar() != null ) {
          lp.remove(rp.getJMenuBar());
        }
        _originalWindow.getContentPane().removeAll();
        _originalWindow.setKeyword(wc.getKeyword());

        JPanel top = wc.getTopPanel();
        _originalWindow.getContentPane().add(top);

        MenuBarControl mbc = wc.getMenuBar();
        if ( mbc != null ) {
          _originalWindow.setMenuBar(mbc);  
        }  

        _originalWindow.setHelpPanel(wc.getHelpPanel());  //SMCook added

        //SMCook still needed in 1.4?
        _originalWindow.invalidate();
        _originalWindow.validate();
        _originalWindow.repaint();  

//      wc.removeAll();  //SMCook commented (speed?)

        wc = null; //SMCook hoping for easier, more thorough garbage collection
        h = null;
      }
    }
    
    /**
     * Reads the GUI builder document and constructs the GUI. This 
     * is a private method called by the <code>peformAction</code>
     * method.
     * 
     * @param filename the path of the document describing the GUI layout
      */
    private WindowControl setUpGUI(String filename) {
      String recentButtonKeyword =
        ButtonPressActionHandler.getMostRecentKeyword();

      AppGUIBuilder builder = new AppGUIBuilder();
      AppGUIDecoder decoder = new AppGUIDecoder();
      decoder.setActionHandlers(builder.getActionHandlers());
      decoder.setFilename(filename);
      Hashtable controls = builder.getGUIControls();
      if ( controls == null ) {
        Console.logMessage(this, "Controls are null");
        return null;
      }
      Enumeration en = controls.elements();
      HelpUpdater hu = new HelpUpdater();
      while (en.hasMoreElements()) {
        GUIControl c = (GUIControl) en.nextElement();

        if ( (c instanceof ArrayGUIControl) ||
           (c instanceof ArraySetGUIControl) ) {
          c.setFont(ClientConstants.getDefaultArrayFont());  
        } 
        
        else if (c instanceof FieldGUIControl ) {
          c.setFont(ClientConstants.getDefaultFieldFont());  
        } 
        
        else {
          c.setFont(ClientConstants.getDefaultLabelFont());  
        }

        c.addGUIControlListener(_guiControlListener);
        c.addGUIControlListener(hu);
        if ( _readOnly ) {
          if(!recentButtonKeyword.equals("PROCEED2PARMS")) {  // special logic
            if(c instanceof TextFieldControl    ||            // doesn't apply
               c instanceof ArrayGUIControl     ||            // everywhere
               c instanceof ArraySetControl     ||
               c instanceof ArraySetGUIControl  ||
               c instanceof IndexedArrayControl ||
               c instanceof ViewRangeArrayControl) {
              try {
                c.getComponent().setBackground(veryLightGray);
              }
              catch(Exception e) {
                Console.logMessage(this, e.getMessage());
              }
            }
          }
          else {
            c.setEditable(false);
            c.setSensitive(false);
          }
        }
      }
      
      if ( builder.getTabPane() != null ) {
        builder.getTabPane().setFont(ClientConstants.getDefaultLabelFont());
        builder.getTabPane().addGUIControlListener(_guiControlListener);
        GUIControllerHelper.putWindowIdTabPaneData(_pid, builder.getTabPane());
      } 
      else {
        Console.logMessage(this, "Tab Pane Control is null");
      }
      
      if ( builder.getTopLevelWindow() != null ) {
        if ( !_replace ) {
          GUIControllerHelper.putWindowIdWindowData(
            _pid, builder.getTopLevelWindow() );  
        }
      } 
      else {
        Console.logMessage(this, "Top Level Window is null");
      }
      
      GUIControllerHelper.putWindowIdControlsData(_pid, controls);
      GUIControllerHelper.putWindowIdHelpSectionData(
        _pid, builder.getHelpSection());

      WindowControl wc = builder.getTopLevelWindow();
      builder.dispose();
      return wc;
    }
        
    /**
     * Sends the keywords of the array components constructed by the builder. 
     * This is a private method called by the <code>peformAction</code>
     * method.
     */
    private void sendArrayNamesMessage() {
      Hashtable h = GUIControllerHelper.getWindowIdToControls();
      Hashtable controls = (Hashtable) h.get(new Integer(_pid));
      if ( controls == null ) {
        Console.logMessage("AppGUIController: Controls are null");
        return;  
      }
      Enumeration en = controls.elements();
      boolean first = true;
      while (en.hasMoreElements()) {
        Object c = en.nextElement();
        if ( c instanceof ArraySetGUIControl) {
          ArraySetGUIControl arraySet = (ArraySetGUIControl) c;
          ArrayGUIControl [] arrays = arraySet.getColumns();
          StringBuffer buf = new StringBuffer();
          for (int i=0;i<arrays.length; i++) {
            if (first) {
              first = false;
            } 
            else {
              buf.append("\n");
            }
            buf.append( ((GUIControl) arrays[i]).getKeyword());
          }
          _commController.transmitMessage(_pid, "ArrayNames",
            arraySet.getKeyword(), buf.toString(), 0, arrays.length-1);
        }
      }
    }                        
    
    private class MyThread extends Thread {
      WindowControl _win;
      public MyThread(WindowControl w) {
        super(w.toString());
        _win = w;
      }
      public void run() {
        _win.setVisible(true);
      }  
    }
  }
  
  /**
   * Inner class that handles "TerminateApp" message.
   */
  private class TerminateAppActionHandler implements XmlActionHandler {
    /**
     * This is invoked by the message decoder when it comes 
     * across a "TerminateApp".
     *
     * @param n the document node to be processed
     */    
    public void performAction(Node n) {
      Hashtable h = GUIControllerHelper.getWindowIdToWindow();
      Enumeration en = h.keys();
      while ( en.hasMoreElements() ) {
        Integer wId = (Integer) en.nextElement();
        int windowId = wId.intValue();
        WindowControl current = (WindowControl) h.get(wId);
        current.dispose();
        GUIControllerHelper.removeWindowIdWindowData(windowId);
        GUIControllerHelper.removeWindowIdTabPaneData(windowId);
        GUIControllerHelper.removeWindowIdHelpSectionData(windowId);
        GUIControllerHelper.removeWindowIdControlsData(windowId);
      }
      System.err.println("Terminate Application");
      _commController = null;  
      _replyActionHandlers.clear();
      _replyActionHandlers = null;
      _guiControlListener = null;
      _currentHelpSection = null;
      _windowUnderFocus = null;
      System.exit(0);
    }  
  }    
  
  /**
   * Action handler for the "Close" action. The "Close" action
   * disposes off a window.
   */
  public class CloseActionHandler implements XmlActionHandler {
    /**
     * Constructs a new action handler.
     * 
     * @param helper the action handler helper
     */
    public CloseActionHandler() {
      super();  
    }
    
    /**
     * Performs the action as specified by the XML node describing the action.
     * This method is invoked by the message decoder. 
     * 
     * @param n the XML node for the message
     */  
    public void performAction(Node n) {
      int windowId = XMLHelper.getIntAttributeValue(n, "windowId");
      WindowControl wc = getTopLevelWindow(windowId);
      wc.dispose();
      GUIControllerHelper.removeWindowIdWindowData(windowId);
      GUIControllerHelper.removeWindowIdTabPaneData(windowId);
      GUIControllerHelper.removeWindowIdHelpSectionData(windowId);
      GUIControllerHelper.removeWindowIdControlsData(windowId);
      _windowUnderFocus = null;
    }    
  }
  
  /**
   * Listens only to the top level window focus events. Updates the 
   * help section object based on the window thats under focus.
   */
  private class HelpUpdater implements GUIControlListener {
    /**
     * This method is invoked when a window or a control gains focus.
     * 
     * @param e the event that is generated when a component gains focus
     */
    public void guiControlChanged(GUIControlEvent e) {

      int type = e.getType();
      if ( type == GUIControlEvent.WINDOW_FOCUS_EVENT) {
        WindowControl wc = (WindowControl) e.getSource();
        _windowUnderFocus = wc;

        int winId = AppGUIController.getWindowIdForWindow(wc);
        if ( winId != ClientConstants.UNDEFINED_WINDOW_ID ) {
          _currentHelpSection = (HelpSection) 
            GUIControllerHelper.getWindowIdToHelpSection().get(
              new Integer(winId));
          updateHelpWindow(winId);
        } 
        else {
          Console.logMessage("AppGUIController: Undefined window ID");
        }
      } 
      
      else if ( type == GUIControlEvent.COMPONENT_FOCUS_EVENT ) {
        if (Preferences.getHelpMode().equals(ClientConstants.FOCUS_HELP_MODE)) {
          updateHelp((GUIControl) e.getSource());
        }
      } 
      
      else if ( type == GUIControlEvent.MOUSE_ENTERED_EVENT ) {
        if (Preferences.getHelpMode().equals(ClientConstants.MOUSE_HELP_MODE)) {
          updateHelp((GUIControl) e.getSource());
        }
      }

    }
    
    /**
     * Updates the help window.
     */
    private void updateHelpWindow(int winId) {
      String winKey = _windowUnderFocus.getKeyword();
      Hashtable h = GUIControllerHelper.getWindowIdToHelpSection();  
      HelpSection hs = (HelpSection) h.get(new Integer(winId));
      String winHelpText = hs.getHelpText(winKey);
      HelpWindowControl.setProcessHelp(winHelpText);
    }
    
    /**
     * Updates the help panel for displaying the help for the
     * component that is under focus. This method gets invoked
     * when a component gains focus. 
     * 
     * @param c the GUI control that has gained the focus
     */
    private void updateHelp(GUIControl c) {
      try {
        String key = c.getKeyword().toUpperCase();

        String t    = _currentHelpSection.getHelpTip(key);                
        String text = _currentHelpSection.getHelpText(key);
        JLabel l = (JLabel) _windowUnderFocus.getHelpPanel().getComponents()[0];

        if ( t != null ) {
          l.setText(t);
        } 
        else {
          l.setText("NULL");
        }
        HelpWindowControl.setContextHelp(text);

        l.invalidate();       //SMCook - only the label needs to be repainted
        l.validate();
        l.repaint();
      } 
      catch ( Exception e ) {
        //Console.logMessage("Exception during tip update " + e.getMessage());
        return;
      }      
    }
  }
    
  /**
   * Inner class which is used by the reply action handlers to get access
   * to the GUI controls.
   */
  public class ActionHandlerHelper {
    /**
     * Returns a control for a given window.
     * 
     * @param windowId the id of the window 
     * @param keyword the keyword of the desired control
     * @return the GUI control object associated with specified keyword
     */
    public GUIControl getControl(int windowId, String keyword) {
      Hashtable controls =
        (Hashtable) GUIControllerHelper.getWindowIdToControls().get(
          new Integer(windowId));
      GUIControl c = (GUIControl) controls.get(keyword.toUpperCase());    
      if ( c == null ) {
        Console.logMessage("AppGUIController: " + 
          "Control is null " + keyword);
      }
      return c;
    }
    
    /**
     * Returns the window control for the specified window id.
     * 
     * @param windowId the id of the window 
     * @return the window control associated with the specified id
     */
    public WindowControl getTopLevelWindow(int windowId) {
      return AppGUIController.getTopLevelWindow(windowId);  
    }
            
    /**
     * Returns the window under focus.
     * 
     * @return the window control under focus
     */
    public WindowControl getWindowUnderFocus() {
      return _windowUnderFocus;
    }
    
    /**
     * Returns the window id for the specified window control.
     * 
     * @param wc the window control whose id is desired
     * @return the window id
     */
    public int getWindowIdForWindow(WindowControl wc) {
      return AppGUIController.getWindowIdForWindow(wc);
    }
                
    /**
     * Returns the tab pane control for the specified window id.
     * 
     * @param windowId the id of the window 
     * @return the tab pane control associated with the specified id
     */
    public TabPaneControl getTabPane(int windowId) {
      Hashtable h =  GUIControllerHelper.getWindowIdToTabPane();
      TabPaneControl tbc = (TabPaneControl) h.get(new Integer(windowId) );
      if ( tbc == null ) {
        Console.logMessage(
          "AppGUIController: " + "Tab Pane Control is null " + windowId);
      }
      return tbc;
    }
  }
}
