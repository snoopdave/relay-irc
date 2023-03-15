
package org.relayirc.swingutil;

import java.awt.*; 
import javax.swing.*; 
import javax.swing.table.*; 

////////////////////////////////////////////////////////////////////////////////

public class SortedHeaderRenderer extends JLabel implements TableCellRenderer {

   public static final int UNSORTED = 1;
   public static final int SORTED_ASCENDING = 2;
   public static final int SORTED_DESCENDING = 3;
	
	private int  _state = UNSORTED;
   private Icon _noIcon = new ImageIcon(getClass().getResource("images/noarrow.gif"));
   private Icon _upIcon = new ImageIcon(getClass().getResource("images/uparrow.gif"));
   private Icon _downIcon = new ImageIcon(getClass().getResource("images/downarrow.gif"));

   //---------------------------------------------------------------------------
	public void setState(int state)  {
      if (state != _state) {
		   _state = state;
         switch (_state)  {
         	case SORTED_ASCENDING: setIcon(_upIcon); break;
         	case SORTED_DESCENDING: setIcon(_downIcon); break;
         	case UNSORTED:
         	default: setIcon(_noIcon); break;
         }
      }
	}
   //---------------------------------------------------------------------------
	public int getState()  {return _state;}
   
   public SortedHeaderRenderer() {
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		setOpaque(true);
      setHorizontalAlignment(SwingConstants.CENTER);
   }
   //---------------------------------------------------------------------------
	public Component getTableCellRendererComponent(JTable table,
	   Object value,boolean isSelected, boolean hasFocus, int row,int column)  {

		setFont(table.getFont());
      
      if (isSelected) {
   		setForeground(UIManager.getColor("Table.selectionForeground"));
   		setBackground(UIManager.getColor("Table.selectionBackground"));
      }
      else {
   		setForeground(UIManager.getColor("TableHeader.foreground"));
   		setBackground(UIManager.getColor("TableHeader.background"));
      
      }
      if (value != null)
   		setText(value.toString());
      else
   		setText("");
		return this; 
	}
}

