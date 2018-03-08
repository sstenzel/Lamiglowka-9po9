import java.awt.* ;
import java.awt.event.* ;
import javax.swing.*;
import java.util.*;
import java.io.*;



// ---------------- GRAFICZNY INTERFACE ----------------------------
//  Obejmuje rozwijane menu, plansze z pol tekstowych oraz przyciski

class GraGI extends JFrame{
	Gra gra;
	Zapisy zapisy = new Zapisy();
	JPanel 	jp1 = new JPanel(),
			jp2 = new JPanel();
	JButton	sprawdzB = new JButton("Sprawdź"),
			cofnijB = new JButton("Cofnij"),
			powtorzB = new JButton("Powtórz");
	JTextField [][]planszaT = new JTextField[9][9];
				// deklaracja, bez nawiasu, dalej wypelnienie
	Color granatowy = new Color(36, 71, 163);
	Color czarny = new Color(26, 26, 26);
	Color szary = new Color(128, 128, 128);
	Color czerwony = new Color(204, 0, 0);
	int wariant;

// ------------------ INICJALIZACJA CZESCI GRAFICZNEJ -----------------
//  Rozmieszczenie obiektow, przypisanie im reakcji na rozne dzialania
//  oraz przypisanie skrotow klawiszowych (wiecej w pomoc.txt)

	GraGI() {
		gra = new Gra();
		wariant = 2;

		setTitle("Dziewięć po dziewięć");
		setSize(400,400);
		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JMenuBar menuBar;
			JMenu menu, submenu;
			JMenuItem menuItem;
			JRadioButtonMenuItem rbMenuItem;
			menuBar = new JMenuBar();
			c.fill = GridBagConstraints.BOTH;
			c.gridwidth = GridBagConstraints.REMAINDER;
			cp.add(menuBar,c);

			menu = new JMenu("Menu");
			menu.setMnemonic(KeyEvent.VK_M);
			menuBar.add(menu);

			submenu = new JMenu("Nowa gra");
			submenu.setMnemonic(KeyEvent.VK_N);
			menuItem = new JMenuItem("Poziom łatwy");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_1, ActionEvent.ALT_MASK));
			menuItem.addActionListener(new nowaGraB(0));
			submenu.add(menuItem);
			menuItem = new JMenuItem("Poziom średni");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_2, ActionEvent.ALT_MASK));
			menuItem.addActionListener(new nowaGraB(1));
			submenu.add(menuItem);
			menuItem = new JMenuItem("Poziom trudny");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_3, ActionEvent.ALT_MASK));
			menuItem.addActionListener(new nowaGraB(2));
			submenu.add(menuItem);
			menu.add(submenu);	// dopiero po dodaniu wszystkich elementow podmenu

			menu.addSeparator();

			ButtonGroup group = new ButtonGroup();
			rbMenuItem = new JRadioButtonMenuItem("Plansza losowa");
			rbMenuItem.setSelected(true);
			rbMenuItem.setMnemonic(KeyEvent.VK_L);
			rbMenuItem.addActionListener(new wariantB(0));
			group.add(rbMenuItem);
			menu.add(rbMenuItem);

			rbMenuItem = new JRadioButtonMenuItem("Wariant 1");
			rbMenuItem.setMnemonic(KeyEvent.VK_1);
			rbMenuItem.addActionListener(new wariantB(1));
			group.add(rbMenuItem);
			menu.add(rbMenuItem);

			rbMenuItem = new JRadioButtonMenuItem("Wariant 2");
			rbMenuItem.setMnemonic(KeyEvent.VK_2);
			rbMenuItem.addActionListener(new wariantB(2));
			group.add(rbMenuItem);
			menu.add(rbMenuItem);

			rbMenuItem = new JRadioButtonMenuItem("Wariant 3");
			rbMenuItem.setMnemonic(KeyEvent.VK_3);
			rbMenuItem.addActionListener(new wariantB(3));
			group.add(rbMenuItem);
			menu.add(rbMenuItem);

			menu.addSeparator();
			menuItem = new JMenuItem("Zapisz");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			menuItem.addActionListener(new zapiszB());
			menu.add(menuItem);

			menuItem = new JMenuItem("Wczytaj");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_R, ActionEvent.CTRL_MASK));
			menuItem.addActionListener(new wczytajB());
			menu.add(menuItem);

			menu.addSeparator();
			menuItem = new JMenuItem("Pomoc");
			menuItem.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_P, ActionEvent.CTRL_MASK));
			menuItem.addActionListener(new pomocB());
			menu.add(menuItem);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		cp.add(jp1, c);

		jp1.setLayout(new GridLayout(9,9));
		jp1.setBorder(BorderFactory.createLoweredBevelBorder());

// ---------------- INICJALIZACJA PLANSZY ------------------------

		Font font = new Font("Arial", Font.PLAIN, 20);
			for(int x =0; x<9; x++){
				for(int y=0; y<9; y++){
					planszaT[x][y] = new JTextField(1);
					jp1.add(planszaT[x][y]);
					if (gra.dajCyfre(x,y) != 0) {
						planszaT[x][y].setText(Integer.toString(gra.dajCyfre(x,y)));
						planszaT[x][y].setEditable(false);
						planszaT[x][y].setForeground(czarny);
					}
					else
						planszaT[x][y].setForeground(szary);
					planszaT[x][y].setHorizontalAlignment(JTextField.CENTER);
					planszaT[x][y].setFont(font);
					planszaT[x][y].setBorder(BorderFactory.createEtchedBorder());
					planszaT[x][y].addKeyListener(new planszaT(x,y));
				}
			}
		c.fill = GridBagConstraints.BOTH;
		cp.add(jp2,c);
			jp2.setLayout(new GridLayout(1,3));
			jp2.add(cofnijB);
			jp2.add(powtorzB);
			jp2.add(sprawdzB);
			sprawdzB.addActionListener(new sprawdzB());
			sprawdzB.setMnemonic('s');
			cofnijB.addActionListener(new cofnijB());
			cofnijB.setEnabled(false);
			cofnijB.setMnemonic('q');
			powtorzB.addActionListener(new powtorzB());
			powtorzB.setEnabled(false);
			powtorzB.setMnemonic('w');
			pokolorujSektory();

		planszaT[4][4].requestFocus();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true) ;
	}

	void pokolorujSektory() {
		Color niebieski = new Color(153, 204, 255);
		Color brzoskwiniowy = new Color(255, 153, 153);
		Color zielony = new Color(191, 255, 128);
		for(int x =0; x<9; x++){
			for(int y=0; y<9; y++){
				switch (gra.plansza[x][y].sektor){
					case 0: planszaT[x][y].setBackground(brzoskwiniowy); break;
					case 1: planszaT[x][y].setBackground(niebieski); break;
					case 2: planszaT[x][y].setBackground(brzoskwiniowy); break;
					case 3: planszaT[x][y].setBackground(zielony); break;
					case 4: planszaT[x][y].setBackground(Color.white); break;
					case 5: planszaT[x][y].setBackground(zielony); break;
					case 6: planszaT[x][y].setBackground(brzoskwiniowy); break;
					case 7: planszaT[x][y].setBackground(niebieski); break;
					case 8: planszaT[x][y].setBackground(brzoskwiniowy); break;
				}
			}
		}
	}

// ---------------------- AKCJA DLA PLANSZY -------------------------
//  Reakcja na znak wpisany w pole planszy
//  jesli strzalki, przesuwa focus we wskazanym kierunku
// 	jesli to cyfra, zostaje zapisana do historii ruchow
//  kazdy inny znak czysci pole i  rozniez zostaje dopisane do historii

	class planszaT implements KeyListener {
		int x,y;
		planszaT(int x,int y) { this.x=x; this.y=y; }
		public void keyTyped(KeyEvent evt) {}
		public void keyPressed(KeyEvent evt) {}
		public void keyReleased(KeyEvent evt) {

			int key = evt.getKeyCode();
	        switch (key) {
	            case KeyEvent.VK_LEFT:
	            	if (y-1 >= 0) {
	            		planszaT[x][y-1].requestFocus();
	            		} return;
	            case KeyEvent.VK_RIGHT:
	            	if (y+1 < 9)
	            		planszaT[x][y+1].requestFocus(); return;
	            case KeyEvent.VK_UP:
	            	if (x-1 >= 0)
	            		planszaT[x-1][y].requestFocus(); return;
	            case KeyEvent.VK_DOWN:
	            	if (x+1 < 9)
	            		planszaT[x+1][y].requestFocus(); return;
	        }

			int cyfra = -1;
				cyfra = dajCyfre(evt.getKeyChar());
		 	if (cyfra > 0 && cyfra <= 9) {
		    	gra.nowyRuch(x,y, cyfra);
		    	planszaT[x][y].setText(gra.toString(x,y)); // dla pewnosci
		    	planszaT[x][y].setForeground(szary);
		    	cofnijB.setEnabled(true);
		    	powtorzB.setEnabled(false);
		 	}
		 	else if (gra.dajCyfre(x,y) != 0) {
		 		gra.nowyRuch(x,y,0);
		 		planszaT[x][y].setText("");
		 	}
		 	else {
		 		planszaT[x][y].setText("");
		 	}
		 }
	}

// ------------------- COFNIECIE RUCHU -----------------------------

	class cofnijB implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!gra.moznaCofnac()) {
				cofnijB.setEnabled(false);
				return;
			}
			Ruch tmp = gra.cofnijRuch();
			if (tmp != null) {
				if (gra.dajCyfre(tmp.x, tmp.y) != 0 ) {
					planszaT[tmp.x][tmp.y].setText(Integer.toString(gra.dajCyfre(tmp.x, tmp.y)));
				}
				else
					planszaT[tmp.x][tmp.y].setText("");
				planszaT[tmp.x][tmp.y].setForeground(szary);
				powtorzB.setEnabled(true);
				planszaT[tmp.x][tmp.y].requestFocus();
			}
			else
				planszaT[4][4].requestFocus();
		}
	}

// ------------------- POWTORZENIE RUCHU ----------------------------------

	class powtorzB implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Ruch tmp = gra.powtorzRuch();
			if (tmp != null) {
				if (gra.dajCyfre(tmp.x, tmp.y) != 0 ) {
					planszaT[tmp.x][tmp.y].setText(Integer.toString(gra.dajCyfre(tmp.x, tmp.y)));
					if (gra.cofniecia == 0)
						powtorzB.setEnabled(false);
				}
				else
					planszaT[tmp.x][tmp.y].setText("");
				planszaT[tmp.x][tmp.y].setForeground(szary);
				cofnijB.setEnabled(true);
				planszaT[tmp.x][tmp.y].requestFocus();
			}
			else
				planszaT[4][4].requestFocus();
		}
	}

// --------------------- SPRAWDZENIE POPRAWNOSCI ---------------------------
// Sprawdza jedynie pola, które sa aktualnie wprowadzone w plansze graficzna,
// ale nie zostaly jeszcze sprawdzone

	class sprawdzB implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Boolean koniecGry = true;
			for(int x =0; x<9; x++){
				for(int y=0; y<9; y++){
					if (planszaT[x][y].isEditable()) {
						if (gra.dajCyfre(x,y) != 0) {
							if ( gra.czyNieprawidlowe(x,y) ) {
								planszaT[x][y].setForeground(czerwony);
								koniecGry = false;
							}
							else {
								planszaT[x][y].setForeground(granatowy);
								planszaT[x][y].setEditable(true);
							}
						}
						else
							koniecGry = false;
					}
				}
			}
			planszaT[4][4].requestFocus();
			if (koniecGry)
				koniecGry();
		}
	}

// ------------------------ NOWA GRA -------------------------------------
// Gracz ma mozliwosc zaznaczenia w menu wariantu planszy (ksztaltu sektorow)
// oraz wyboru poziomu gry dla nowej rozgrywki

	class nowaGraB implements ActionListener {
		int poziom;
		nowaGraB(int poziom) { this.poziom = poziom; }
		public void actionPerformed(ActionEvent e) {
			Object[] opcje = {"Zapisz", "Nie zapisuj", "Anuluj"};
			int n = JOptionPane.showOptionDialog(null,
				"Czy chcesz zapisać obecną grę?",
				"Zapis obecnej gry",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				opcje,
				opcje[0]);
			if (n == 0) {
				new zapiszB().actionPerformed(e);
			}
			else if (n==2) {
				return;
			}
			gra = new Gra(wariant, poziom);
			nowaPlansza();
		}
	}

	class wariantB implements ActionListener {
		int x;
		wariantB(int x) { this.x = x; }
		public void actionPerformed(ActionEvent e) {
			wariant = x;
		}
	}

// --------- ODSWIEZENIE PLANSZY PO WCZYTANIU NOWEJ ROZGRYWKI -----------------
// Odswieza plansze wedlug danych z zapisu gry lub nowej gry.
// Wczytuje cyfry z bazy podstawowej, nastepnie te wpisane przez uzytkownika,
// nadaje kolor wedlug sektorow,
// sprawdza poprawnosc

	void nowaPlansza() {
		pokolorujSektory();
		for(int x =0; x<9; x++){
			for(int y=0; y<9; y++){
				if ( gra.baza[x][y] != 0 ) {
					planszaT[x][y].setText(Integer.toString(gra.baza[x][y]));
					planszaT[x][y].setForeground(czarny);
					planszaT[x][y].setEditable(false);
				}
				else if  (gra.dajCyfre(x,y) != 0) {
					planszaT[x][y].setText(Integer.toString(gra.dajCyfre(x,y)));
					planszaT[x][y].setEditable(true);
					planszaT[x][y].setForeground(szary);
				}
				else {
					planszaT[x][y].setEditable(true);
					planszaT[x][y].setForeground(szary);
					planszaT[x][y].setText("");
				}
			}
		}
		if (gra.moznaCofnac())
			cofnijB.setEnabled(true);
		else
			cofnijB.setEnabled(false);
		if (gra.cofniecia == 0)
			powtorzB.setEnabled(false);
		else
			powtorzB.setEnabled(true);
		sprawdzB.doClick();
	}

// ----------------- ZAPIS I ODCZYT ROZGRYWKI ----------------------

	class zapiszB implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String nazwa = JOptionPane.showInputDialog(null, "Wprowadź nazwę",
					"Zapis gry", JOptionPane.INFORMATION_MESSAGE);
			if (nazwa != null)
				zapisy.nowy(nazwa, gra);
		}
	}

	class wczytajB implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String nazwa = JOptionPane.showInputDialog(null, "Wprowadź nazwę",
					"Wczytywanie gry", JOptionPane.INFORMATION_MESSAGE);
			if (nazwa != null) {
				Gra wczytanaGra = zapisy.wczytaj(nazwa);
				if (wczytanaGra != null) {
					gra = wczytanaGra;
					nowaPlansza();
				}
			}
		}
	}

// ----------------------  OKIENKO POMOCY -------------------------

	class pomocB implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String tekst = gra.wczytajPomoc();
			if (tekst != null) {
				noweOkno(tekst, "Pomoc");
			}
		}
	}

	void noweOkno (String tekst, String nazwa) {
		JFrame frame = new JFrame(nazwa);
		frame.setSize(400,600);
		Container cp = frame.getContentPane();
		JTextArea tekstArea = new JTextArea(tekst);
		JScrollPane tekstScrollPane = new JScrollPane(tekstArea);
		tekstArea.setLineWrap(true);
		tekstArea.setEditable(false);
		tekstArea.setForeground(Color.black);
		cp.add(tekstScrollPane);
		frame.setLocation(10,10);
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

	int dajCyfre(char c) {
		try{
				return Character.getNumericValue(c);
		}catch (NumberFormatException e)
			{	System.out.println("Niepoprawny format liczby"); return 0 ; }
	}

	void koniecGry() {
		JOptionPane.showMessageDialog(null, "WYGRAŁEŚ! Gratulacje :D");
	}

}
