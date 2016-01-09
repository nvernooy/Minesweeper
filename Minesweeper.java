  /*
    Program to play the classic Minesweeper game
  */
	import javax.swing.JButton;
   import javax.swing.JFrame;
   import javax.swing.JLabel;
   import javax.swing.JOptionPane;
   import javax.swing.JPanel;
   import javax.swing.UIManager;
   import javax.swing.Icon;
   import javax.swing.ImageIcon;
   import javax.swing.border.LineBorder;
   import java.awt.BorderLayout;
   import java.awt.event.ActionEvent;
   import java.awt.event.ActionListener;
   import java.awt.event.MouseAdapter;
   import java.awt.event.MouseEvent;
   import java.awt.Color;
   import java.awt.Component;
   import java.awt.FlowLayout;
   import java.awt.GridLayout;
   import java.awt.Insets;
   import java.awt.Point;
   import java.awt.Graphics;
   import java.awt.Image;
   import java.awt.Dimension;
   import java.util.ArrayList;
   import java.util.List;


    public class Minesweeper extends JPanel implements ActionListener
   {
    // declare global variables
      private static final int totmines = 10;
      private int rowlen = 10, collen = 9, tot = rowlen * collen;
      private JPanel panel = new JPanel (new GridLayout (rowlen, collen));
      private JLabel mines = new JLabel (totmines + "");
      private JButton restart = new JButton ("Restart");

      private ImageIcon mineicon = new ImageIcon ("mine.gif");
      private ImageIcon flagicon = new ImageIcon ("flag.gif");
      private ImageIcon xicon = new ImageIcon ("xicon.gif");
      private Graphics g;

    // declare global states that are use for varying states of
    // the game
       public static enum GameState
      {
         Playing, Finished
      }


    // set the gamestate to playing
      private GameState state = GameState.Playing;


    // main, declares jframe with OS look and feel
       public static void main (String args[]) throws Exception
      {
        JFrame window = new JFrame ("MineSweeper");
        // check the OS
        // if (System.getProperty("os.name").toLowerCase().contains("windows")){
        //   UIManager.setLookAndFeel ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        // }
        // else{
        //   UIManager.setLookAndFeel ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        // }
        // set layout
        window.setLayout (new BorderLayout ());
        // add new minesweeper object
        window.add (new Minesweeper ());

        // set settings
        window.setResizable (false);
        window.setSize (280, 300);
        window.setLocationRelativeTo (null);
        window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        window.setVisible (true);
      }


    // minesweeper object
       public Minesweeper ()
      {
      // set the layout and add the main panel
         setLayout (new BorderLayout ());
         add (panel, BorderLayout.CENTER);

      // call method to create the mines
         minefield ();
      // and add the contol panel
         controlPanel ();
      }


    // create the minefield, basically a matrix of buttons
       private void minefield ()
      {
      // a list of all the location of the mines
         List < Point > mines = new ArrayList < Point > ();

      // loop through the matrix
         for (int row = 0 ; row < rowlen ; row++)
         {
            for (int col = 0 ; col < collen ; col++)
            {
            // create a new JButton
            // mines, tot no of mines, and a Point as paramater
               JButton btn = getField (mines, tot,
                  // Point being an object with x and y coordinates
                      new Point (row, col)
                     {
                     // redefine the toString and equals methods
                         @ Override
                         public String toString (){
                           return (int) getX () + ", " + (int) getY ();}

                         @ Override
                         public boolean equals (Object obj){
                           return ((Point) obj).getX () == getX () && ((Point) obj).getY () == getY ();}
                     }
                  );
            // add the button to the panel
               panel.add (btn);
            }
         }
      // while the bombs are less than specified
         while (mines.size () < totmines)
            newMinesList (mines, panel.getComponents ());
      // loop through the components
         for (Component c:panel.getComponents ())
            surroundingMines ((Field) c, panel.getComponents ());

      }


    // add a control panel
       private void controlPanel ()
      {
        // create new panel
        JPanel tpnl = new JPanel (new FlowLayout (FlowLayout.CENTER));
        // add a restart button
        restart.setBackground(new Color(163, 184, 204));
        tpnl.add (restart);
        // add the current minecount label
        tpnl.add (new JLabel ("No of mines left:"));
        tpnl.add (mines);
        add (tpnl, BorderLayout.SOUTH);
        // add actionlitener to the restart button
        restart.addActionListener (this);
      }


    // make a new list of the positions of all the mines
       private void newMinesList (List < Point > locations, Component[] components)
      {
      // loop through all the buttons
         for (Component c:components)
         {
         // get their positions
            Point location = ((Field) c).getPosition ();
         // set a new random position for the mine
            int current = (int) ((location.x) * collen + location.getY ());
            int minelocation = (int) (Math.random () * tot);
         // if they are the same set the positionas a mine
            if (minelocation == current)
            {
               ((Field) c).setMine (true);
            // and add it to the list of mine positions
               locations.add (((Field) c).getPosition ());
               return;
            }
         }
      }


    // when a field is pressed the minecounts of the surrounding fields needs to be shown
       private void surroundingMines (Field btn, Component[] components)
      {
      // get the positions of the surrounding areas
         Point[] points = getFields (btn.getPosition ());

      // loop through the array of positions
         for (Point p:points)
         {
         // get the button at the current position
            Field b = getFieldAt (components, p);
         // if it is a mine
            if (b != null && b.isMine ())
            // increment the bomb count
               btn.setminecount (btn.getminecount () + 1);
         }
      // and update the surrounding field
         btn.setText (btn.getminecount () + "");
      }


    // when the surrounding area is clear the field needs to be opened
       private void clearFields (Point current)
      {
      // get the surrounds
         Point[] points = getFields (current);

      // loop through the surrounding fields
         for (Point p:
         points)
         {
         // get the button
            Field b = getFieldAt (panel.getComponents (), p);
         // if there are no surrounding bombs and it is not a bomb itself the field is cleared
            if (b != null && b.getminecount () == 0 && b.getState () != State.Clicked && b.getState () != State.Flagged && b.isMine () == false)
            {
            //set is as clicked
               b.setState (State.Clicked);
            // go recursively through the other fields
               clearFields (b.getPosition ());
               b.updateUI ();
            }
         // if there are other bombs but it is not a bomd itself
            if (b != null && b.getminecount () > 0 && b.getState () != State.Clicked && b.getState () != State.Flagged && b.isMine () == false)
            {
            // disable the button, set it as clicked. the recursion stops here
               b.setEnabled (false);
               b.setState (State.Clicked);
               b.updateUI ();
            }
         }
      }


    // return the current field object
       private Field getField (List < Point > minesloc, int tot, Point location)
      {
      // make a new button at the current location and set settings
         Field btn = new Field (location);
         btn.setMargin (new Insets (0, 0, 0, 0));
         btn.setFocusable (false);
      // if less mines are listed than set
         if (minesloc.size () < totmines)
         {
         // if the button is a mine, it is set as true and added to the list
            if (isMine ())
            {
               btn.setMine (true);
               minesloc.add (location);
            }
         }
      // add a mouse listener to the button
         btn.addMouseListener (
            // add a mouselistener for more specified interaction
                new MouseAdapter ()
               {
               // override the superclass method
                   @ Override
                   // if the mouse is clicked the game starts playing
                   public void mouseClicked (MouseEvent mouseEvent)
                  {
                     if (state != GameState.Playing)
                     {
                        state = GameState.Playing;
                     }

                  //if the clicked button is disabled (set false), ignore
                     if (((Field) mouseEvent.getSource ()).isEnabled () == false)
                        return;
                  // if the left mouse button is clicked
                     if (mouseEvent.getButton () == MouseEvent.BUTTON1)
                     {
                     // if the button is marked as a bomb
                        if (((Field) mouseEvent.getSource ()).getState () == State.Flagged)
                        {
                        // return it back to its default state
                           ((Field) mouseEvent.getSource ()).setState (State.Defualt);
                        // increment the mine count
                           mines.setText ((Long.parseLong (mines.getText ()) + 1) + "");
                           ((Field) mouseEvent.getSource ()).updateUI ();
                           return;
                        }
                     // otherwise set the button as clicked
                        ((Field) mouseEvent.getSource ()).setState (State.Clicked);
                     // if it is a mine, it explodes
                        if (((Field) mouseEvent.getSource ()).isMine ())
                        {
                           explode ();
                           return;
                        }
                        // else if the surrounding areas are also clear
                        else if (((Field) mouseEvent.getSource ()).getminecount () == 0)
                        {
                        // update them
                           clearFields (((Field) mouseEvent.getSource ()).getPosition ());
                        }
                        if (!getGameSate ())
                        // enable the button
                           ((Field) mouseEvent.getSource ()).setEnabled (false);
                     } // if the right button is clicked
                     else if (mouseEvent.getButton () == MouseEvent.BUTTON3)
                     {
                     // if the button is already flagged
                        if (((Field) mouseEvent.getSource ()).getState () == State.Flagged)
                        {
                        // unflag it and increment the mine count
                           ((Field) mouseEvent.getSource ()).setState (State.Defualt);
                           mines.setText ((Long.parseLong (mines.getText ()) + 1) + "");
                        }
                        else
                        {
                        // else mark the button and decrease the mine count
                           ((Field) mouseEvent.getSource ()).setState (State.Flagged);
                           mines.setText ((Long.parseLong (mines.getText ()) - 1) + "");
                        }
                     }
                     ((Field) mouseEvent.getSource ()).updateUI ();
                  }
               }
            );
      // return the changed btn
         return btn;
      }


    // restart the game when the button is pressed
    // reset all the fields
       private void restart ()
      {
         state = GameState.Playing;
         panel.removeAll ();
         minefield ();
         panel.updateUI ();
         mines.setText ("" + totmines);
         mines.updateUI ();
      }


    // check the state of the game
       private boolean getGameSate ()
      {
         boolean won = false;
      // loop through all the components
         for (Component c:panel.getComponents ())
         {
            Field b = (Field) c;
            if (b.getState () != State.Clicked)
            {
            // if all the buttons are clicked the game ends
               if (b.isMine ())
                  won = true;
               else
                  return false;
            }
         }
      // if the player has won the game finishes
         if (won)
         {
            state = GameState.Finished;
            for (Component c:
            panel.getComponents ())
            {
               Field b = (Field) c;
            // flag all the leftover bombs
               if (b.isMine ())
               {
                  b.setState (State.Flagged);
               }
            // disable all the buttons
               b.setEnabled (false);

            }
         // display a message that the player has won
            JOptionPane.showMessageDialog (this, "You are a winner", "Finished", JOptionPane.INFORMATION_MESSAGE, null);
         }
         return won;
      }


    // when a mine is found
       private void explode ()
      {
         int count = 0;
      // loop through all the buttons and disable them
         for (Component c:panel.getComponents ())
         {
            ((Field) c).setEnabled (false);
            ((Field) c).transferFocus ();
         // if it is a mine click it and increase the amount of mines exploded
            if (((Field) c).isMine () && ((Field) c).getState () != State.Flagged)
            {
               ((Field) c).setState (State.Clicked);
               ((Field) c).updateUI ();
               count++;
            }
         // if it is not a mine but the player flagged it, it is wrongly marked
            if (((Field) c).isMine () == false && ((Field) c).getState () == State.Flagged)
               ((Field) c).setState (State.WrongFlagged);
         }
      // display the no of explosions in the label
         mines.setText ("" + count);
         mines.updateUI ();
      // finish the game
         state = GameState.Finished;
         JOptionPane.showMessageDialog (this, "You have exploded a mine\nYou have lost", "Game Over", JOptionPane.INFORMATION_MESSAGE, null);
      // disable all the buttons
         for (Component c:panel.getComponents ())
         {
            Field b = (Field) c;
            b.setEnabled (false);
         }
      }


    // set this as a mine
       private boolean isMine (){
         return ((int) (Math.random () * rowlen) == 1);}


    // declare global states that are use for varying states of
    // the buttons
       public static enum State
      {
         Clicked, Flagged, Defualt, WrongFlagged
      }


    // the Field class, extending the JButton class
       class Field extends JButton
      {
      // set default variables
         private boolean isMine = false;
         private State state = State.Defualt;
         private int minecount = 0;
         private Point position = null;


      // constuctor
          public Field (Point position)
         {
         // setting the position and text of the button
            setPosition (position);
            setText (position.toString ());
            // this.setPreferredSize(new Dimension(20, 20));
            // this.setBorder( new LineBorder(Color.BLACK) );
         }


      // set the state of this button
          public void setState (State state)
         {
            this.state = state;
            if (getminecount () == 0 && !isMine)
            {
               setEnabled (false);
            }
         }


      // get the state
          public State getState ()
         {
            return state;
         }


      // get this buttons position
          public Point getPosition ()
         {
            return position;
         }


      // set the position
          public void setPosition (Point position)
         {
            this.position = position;
         }


      // get the no of mines around this field
          public int getminecount ()
         {
            return minecount;
         }


      // set the no of mines
          public void setminecount (int minecount)
         {
            this.minecount = minecount;
         }


      // get if it is a mine
          public boolean isMine ()
         {
            return isMine;
         }


      //set if it is a mine
          public void setMine (boolean isMine)
         {
            this.isMine = isMine;
         }

       // override the getBackground method
          @ Override
          public Color getBackground ()
         {
         // if it is clicked
            if (state == State.Clicked)
            {
            //  and a bomb
               if (isMine)
                  return Color.RED;
            // if not a bomb with surrounding bombs
               if (getminecount () > 0)
                  return new Color(255, 255, 204);
            }
         // if it is still enabled
            if (isEnabled ())
               return new Color(166, 166, 166);
            else
               return super.getBackground ();
         }

      // override the JButton method of getIcon
          @ Override
          // return a string depending on what state the button is in
          public Icon getIcon ()
         {
         // if it is flagged
            if (state == State.Flagged)
               return flagicon;

         // if it is clicked
            if (state == State.Clicked)
            {
            // and a bomb
               if (isMine)
                  return mineicon;
               // else if its not a bomb with surrounding bombs
            }
         // if it has been wrongly marked
            if (state == State.WrongFlagged)
               return xicon;

            return super.getIcon ();
         }

      // override the JButton method of setText
      // return a string depending on what state the button is in
          @ Override
          public String getText (){
            if (state == State.Defualt) {
               return "";}
            if (state == State.Flagged) {
               return "\u00B6";}
            if (state == State.Clicked) {
               if (isMine) {
                  return "<html><font size='16'><b>*</b></font></html>";
               }
               else
               {
                  if (getminecount() > 0)
                     return getminecount() + "";
                  else
                     return "";
               }
            }
            return super.getText ();
         }

          @Override
          // override the getPreferredSize function to make square buttons
          public Dimension getPreferredSize() {
              Dimension d = super.getPreferredSize();
              int s = (int)(d.getWidth()<d.getHeight() ? d.getHeight() : d.getWidth());
              return new Dimension (s,s);
          }
      }


    //get the surrounding positions in an array
       private Point[] getFields (Point cPoint)
      {
         int cX = (int) cPoint.getX ();
         int cY = (int) cPoint.getY ();
         Point[] points = {new Point (cX - 1, cY - 1), new Point (cX - 1, cY), new Point (cX - 1, cY + 1), new Point (cX, cY - 1), new Point (cX, cY + 1), new Point (cX + 1, cY - 1), new Point (cX + 1, cY), new Point (cX + 1, cY + 1) };
         return points;
      }


    // return the button at the specifid position
       private Field getFieldAt (Component[] components, Point position)
      {
         for (Component btn:components)
            if ((((Field) btn).getPosition ().equals (position)))
               return (Field) btn;

         return null;
      }


    // if an action is performed
       public void actionPerformed (ActionEvent actionEvent)
      {
      // if the restart button is pressed restart the game
         if (actionEvent.getSource () == restart)
            restart ();
      }
   }
