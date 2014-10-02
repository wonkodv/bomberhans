package org.hanstool.bomberhans.mapeditor;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.hanstool.bomberhans.client.GUI_graphics;

public class MapEditor extends JFrame
{
	private static final long	serialVersionUID	= -1260493252849620356L;
	GUI_graphics.FieldPanel		field;
	byte						currentCellType;
	private JButton				newBTN;
	private JButton				loadBTN;
	private JButton				saveBTN;
	JTextField					sizeXTFD;
	JTextField					sizeYTFD;
	private JTextField			nameTFD;
	private JTextField			woodStayTFD;
	private JTextField			specialTFD;
	
	static
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception localException)
		{
		}
	}

	public MapEditor(File map)
	{
		this();
		loadMap(map);
	}
	
	public MapEditor()
	{
		setLayout(null);
		
		field = new GUI_graphics.FieldPanel();
		add(field);
		field.setBounds(115, 115, 300, 300);
		field.setFocusable(false);
		
		MouseAdapter m = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				MapEditor.this.onFieldClick(e);
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				MapEditor.this.onFieldClick(e);
			}
		};
		field.addMouseListener(m);
		field.addMouseMotionListener(m);
		
		field.setCellTypes(genDefMap(19, 21));
		
		byte[] cellTypes = { 1, 2, 3, 21, 19, 20, 16, 17, 18 };
		
		for(byte i = 0; i < cellTypes.length; i = (byte) (i + 1))
		{
			BufferedImage img = field.imgBuffer.getcellImage(cellTypes[i]);
			HansButton b = new HansButton(img, cellTypes[i]);
			
			b.setBounds(10 + (3 + i) * 35, 80, 30, 30);
			add(b);
		}
		
		loadBTN = new JButton("load");
		loadBTN.setBounds(10, 10, 65, 30);
		add(loadBTN);
		loadBTN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				MapEditor.this.loadMap();
			}
		});
		saveBTN = new JButton("save");
		saveBTN.setBounds(10, 45, 65, 30);
		add(saveBTN);
		saveBTN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				MapEditor.this.saveMap();
			}
		});
		sizeXTFD = new JTextField("19");
		sizeXTFD.setBounds(10, 115, 30, 30);
		add(sizeXTFD);
		
		sizeYTFD = new JTextField("19");
		sizeYTFD.setBounds(45, 115, 30, 30);
		add(sizeYTFD);
		
		newBTN = new JButton("new");
		newBTN.setBounds(10, 150, 65, 30);
		add(newBTN);
		newBTN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				field.setCellTypes(MapEditor.this.genDefMap(Integer.parseInt(sizeXTFD.getText()), Integer.parseInt(sizeYTFD.getText())));
			}
		});
		{
			JLabel lbl = new JLabel("  MapName:");
			lbl.setBounds(115, 10, 170, 30);
			add(lbl);
		}

		{
			nameTFD = new JTextField("My Awsome Map");
			nameTFD.setBounds(115, 45, 170, 30);
			add(nameTFD);
		}
		{
			JLabel lbl = new JLabel("WoodStay%");
			lbl.setBounds(290, 10, 65, 30);
			add(lbl);
		}
		{
			woodStayTFD = new JTextField("60");
			woodStayTFD.setBounds(290, 45, 65, 30);
			add(woodStayTFD);
		}
		{
			JLabel nameLBL = new JLabel("Special%");
			nameLBL.setBounds(395, 10, 65, 30);
			add(nameLBL);
		}
		{
			specialTFD = new JTextField("30");
			specialTFD.setBounds(395, 45, 65, 30);
			add(specialTFD);
		}
		setDefaultCloseOperation(0);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				int s = JOptionPane.showConfirmDialog(MapEditor.this, "save?");
				if(s == 2)
				{
					return;
				}
				if(s == 0)
				{
					MapEditor.this.saveMap();
				}
				System.exit(0);
			}
		});
		setBounds(300, 300, 1000, 800);
		setVisible(true);
	}
	
	byte[][] genDefMap(int sizeX, int sizeY)
	{
		byte[][] bytes = new byte[sizeX][sizeY];
		for(int y = 0; y < sizeY; y++ )
		{
			for(int x = 0; x < sizeX; x++ )
			{
				byte b;
				if(x == 0 || x == sizeX - 1 || y == 0 || y == sizeY - 1)
				{
					b = 2;
				}
				else
				{
					if((x == 1 || x == sizeX - 2) && (y == 1 || y == sizeY - 2))
					{
						b = 21;
					}
					else
					{
						if((x == 2 || x == sizeX - 3) && (y == 1 || y == sizeY - 2))
						{
							b = 1;
						}
						else
						{
							if((x == 1 || x == sizeX - 2) && (y == 2 || y == sizeY - 3))
							{
								b = 1;
							}
							else
							{
								if(x % 2 == 0 && y % 2 == 0)
								{
									b = 2;
								}
								else
								{
									b = 3;
								}
							}
						}
					}
				}
				bytes[x][y] = b;
			}
		}
		return bytes;
	}
	
	protected void loadMap(File f)
	{
		try
		{
			MapLoader ml = new MapLoader(f);
			
			nameTFD.setText(ml.getMapName());
			sizeXTFD.setText(Byte.toString(ml.getSizeX()));
			sizeYTFD.setText(Byte.toString(ml.getSizeY()));
			woodStayTFD.setText(Byte.toString(ml.getWoodStayP()));
			specialTFD.setText(Byte.toString(ml.getSpecialP()));
			
			field.setCellTypes(ml.getCellTypes());
			field.repaint();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, e);
		}

	}
	
	protected void loadMap()
	{
		JFileChooser chooser = new JFileChooser(new File("."));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("BomberHansMap", new String[] { "bhMap" });
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == 0)
		{
			loadMap(chooser.getSelectedFile());
		}
		
	}
	
	protected void onFieldClick(MouseEvent e)
	{
		byte x = (byte) ((e.getX() - 5) / 25);
		byte y = (byte) ((e.getY() - 5) / 25);
		byte c;
		if(e.getButton() == 3 || e.getModifiersEx() == 4096)
		{
			c = 1;
		}
		else
		{

			if(e.getButton() == 1 || e.getModifiersEx() == 1024)
			{
				c = currentCellType;
			}
			else
			{
				return;
			}
		}
		if(x == 0 || y == 0 || x == field.getSizeX() - 1 || y == field.getSizeY() - 1)
		{
			c = 2;
		}
		try
		{
			if(field.getCellType(x, y) != c)
			{
				field.updateCell(x, y, c);
				field.repaint();
			}
		}
		catch(ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
		{
		}
	}
	
	protected void saveMap()
	{
		JFileChooser chooser = new JFileChooser(new File("."));
		chooser.setSelectedFile(new File("." + File.separator + nameTFD.getText() + ".bhMap"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("BomberHansMap", new String[] { "bhmap" });
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(this);
		if(returnVal != 0)
		{
			return;
		}
		
		try
		{
			String fileName = chooser.getSelectedFile().getPath();
			if( !filter.accept(chooser.getSelectedFile()))
			{
				fileName = fileName + ".bhmap";
			}
			DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
			
			out.writeByte(1);
			out.writeUTF(nameTFD.getText());
			out.writeByte(Byte.parseByte(woodStayTFD.getText()));
			out.writeByte(Byte.parseByte(specialTFD.getText()));
			out.writeByte(field.getSizeX());
			out.writeByte(field.getSizeY());
			
			for(byte x = 0; x < field.getSizeX(); x = (byte) (x + 1))
			{
				for(byte y = 0; y < field.getSizeY(); y = (byte) (y + 1))
				{
					out.write(field.getCellType(x, y));
				}
			}
			
			out.close();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, e);
		}
	}
	
	class HansButton extends JButton
	{
		private static final long	serialVersionUID	= 6468973502567473815L;
		BufferedImage				img;
		byte						cellType;
		
		public HansButton(BufferedImage img, final byte cellType)
		{
			this.img = img;
			this.cellType = cellType;
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e)
				{
					currentCellType = cellType;
				}
			});
		}
		
		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			g.drawImage(img, 2, 2, null);
		}
	}
}
