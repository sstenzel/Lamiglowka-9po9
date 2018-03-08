import java.util.*;
import java.io.*;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.*;

/*
 *
 *  Nazwa gry: 	DZIEWIEC PO DZIEWIEC
 *  Autor:	Sonia Stenzel
 *
 *  Zasady gry:
 *  			nalezy wypelnic puste pola planszy cyframi od 1 do 9, tak aby:
 *  			- w kazdym rzedzie, kolumnie oraz sektorze wystepowalo
 *  	 		 dziewiec roznych cyfr
 *
 *  	SEKTORY	 		- obszary oznaczone roznymi kolorami,
 *	          			  kazdy obejmuje 9 pól
 *
 *	    WARIANT PLANSZY	- ułożenie pól dziewięciu sektorów
 *
 *	    POZIOM GRY		- łatwy:  30 wylosowanych liczb początkowych
 *	       				- średni: 25 liczb
 *	        			- trudny: 20 liczb
 *
 *  Zalaczono: pomoc.txt - zawiera instrukcje oraz spis skrotow klawiszowych
 *
 *  Spis tresci:
 * 	1. MECHANIZM GRY
 * 		 1.1 UTWORZENIE SEKTOROW
 * 		 1.2 WYGENEROWANIE PLANSZY
 * 		 1.3 SPRAWDZENIE POPRAWNOSCI
 * 		 1.4 NOWY RUCH
 * 		 1.5 COFNIJ RUCH
 * 		 1.6 POWTORZ RUCH
 * 		 1.7 WCZYTANIE POMOCY
 * 	2. KLASY POMOCNICZE: POLE I RUCH
 * 	3. ZAPIS I ODCZYT
 * 	4. GRAFICZNY INTERFACE - (w osobnym pliku)
 * 		 4.1 INICJALIZACJA CZESCI GRAFICZNEJ
 * 		 4.2 INICJALIZACJA PLANSZY
 * 		 4.3 AKCJA DLA PLANSZY
 * 		 4.4 COFNIECIE RUCHU
 *  	 	4.5 POWTORZENIE RUCHU
 *  	 	4.6 SPRAWDZENIE POPRAWNOSCI
 *  	 	4.7 NOWA GRA
 * 		 4.8 ODSWIEZENIE PLANSZY PO WCZYTANIU NOWEJ ROZGRYWKI
 *  	 	4.9 ZAPIS I ODCZYT
 *  	 	4.10 OKIENKO POMOCY
 *  	5. MAIN
 *
 */



//------------------------- MECHANIZM GRY ---------------------------------
//  baza[][] 	- zapis cyfr wylosowanych dla danej rozgrywki
//  plansza[][]	- baza oraz cyfry wprowadzone przez gracza
//  sektory[][]	- zbior odnosnikow do poszczegolnych pol planszy
//				- sektory[numer sektora][index]
//  historia	- zapis ruchow wykonanych przez gracza

class Gra implements Serializable{
	Pole [][]plansza = new Pole[9][9];
	Pole [][]sektory = new Pole[9][9];
	ArrayList<Ruch> historia;
	int [][]baza = new int[9][9];
	int cofniecia;

	Gra() {
		historia = new ArrayList<Ruch>();
		cofniecia = 0;
		for(int x =0; x<9; x++){
			for(int y=0; y<9; y++){
				plansza[x][y] = new Pole(0,-1);
				baza[x][y] = 0;
			}
		}
		utworzSektory(0);
		wygenerujMape(1);
	}

	Gra(int wariant, int poziom) {
		historia = new ArrayList<Ruch>();
		cofniecia = 0;
		for(int x =0; x<9; x++){
			for(int y=0; y<9; y++){
				plansza[x][y] = new Pole(0,-1);
				baza[x][y] = 0;
			}
		}
		utworzSektory(wariant);
		wygenerujMape(poziom);
	}

// ------------------------- UTWORZENIE SEKTOROW ----------------------------------
// W tablicach tymczasowych, w roznych ulozeniach,
// znajduja sie numery sektorow dla poszczegolnych wspolrzednych.
// - nadanie sektorow
// - przypisanie tablicy sektory[][] odpowiednikow z plansza[][]

	void utworzSektory(int wariant) {
		if (wariant == 0) {
			wariant = new Random().nextInt(3)+1;
		}
		int [][]tmp = new int[9][9];
		switch (wariant) {
			case 1:
					int [][]tmp1 =
					{{0,0,1,1,1,1,1,2,2},
					{0,0,0,1,1,1,2,2,2},
					{0,3,0,0,1,2,2,5,2},
					{3,3,0,4,4,2,5,5,5},
					{3,3,3,4,4,4,5,5,8},
					{3,3,6,6,4,4,4,5,8},
					{3,6,6,7,7,4,8,5,8},
					{6,6,7,7,7,7,8,5,8},
					{6,6,6,7,7,7,8,8,8}};
					tmp = tmp1;
				 	break;
			case 2:
					int [][]tmp2 =
					{{0,0,0,1,1,1,2,2,2},
					{0,0,0,1,1,1,2,2,2},
					{0,0,0,1,1,1,2,2,2},
					{3,3,3,4,4,4,5,5,5},
					{3,3,3,4,4,4,5,5,5},
					{3,3,3,4,4,4,5,5,5},
					{6,6,6,7,7,7,8,8,8},
					{6,6,6,7,7,7,8,8,8},
					{6,6,6,7,7,7,8,8,8}};
					tmp = tmp2;
					break;
			case 3:
					int [][]tmp3 =
					{{0,0,0,0,1,2,2,2,2},
					{0,0,0,1,1,1,2,2,2},
					{0,0,1,1,1,1,1,2,2},
					{3,3,3,3,4,5,5,5,5},
					{3,3,3,4,4,4,5,5,5},
					{3,3,4,4,4,4,4,5,5},
					{6,6,7,7,7,7,7,8,8},
					{6,6,6,7,7,7,8,8,8},
					{6,6,6,6,7,8,8,8,8}};
					tmp = tmp3;
					break;
			default:
					utworzSektory(1);
					return;
		}
		int []index = new int[9];
		for(int x =0; x<9; x++){ index[x] = 0;}
		int sektor;
		for(int x =0; x<9; x++){
			for(int y=0; y<9; y++){
				sektor = tmp[x][y];
				sektory[sektor][index[sektor]] = plansza[x][y];
				index[sektor]++;
			}
		}
		for(int s =0; s<9; s++){
			for(int i=0; i<9; i++){
				sektory[s][i].sektor = s;
			}
		}
	}

// -------------------- WYGENEROWANIE PLANSZY  ------------------------------
// losuje wspolrzedne, sprawdza czy miejsce jest wolne
// nastepnie losuje cyfre i sprawdza czy nie koliduje z wczesniej
// prowadzonymi cyframi

	void wygenerujMape(int poziom) {
		Random random = new Random();
		int x,y, cyfra, ilosc;
		switch(poziom) {
			case 0: ilosc = 30; break;
			case 1: ilosc = 25; break;
			case 2: ilosc = 20; break;
//			case 3: ilosc = 0; break;
			default: ilosc = 25; break;
		}
		for(int i =0; i<ilosc; i++){
			do {
				x = random.nextInt(9);
				y = random.nextInt(9);
			} while(czyZajete(x,y));
			do {
				cyfra = random.nextInt(9)+1;
			} while(czyNieprawidlowe(x,y, cyfra));
			plansza[x][y].zawartosc = cyfra;
			baza[x][y] = cyfra;
		}
	}

	Boolean czyZajete(int x, int y) {
		if (plansza[x][y].zawartosc == 0 )
			return false;
		return true;
	}

// ---------------------- SPRAWDZENIE POPRAWNOSCI ------------------------------
// Sprawdza czy pole wskazane przez GI nie koliduje z innymi

	Boolean czyNieprawidlowe(int x, int y) {
		int cyfra = plansza[x][y].zawartosc;
		int sektor = plansza[x][y].sektor;
		for(int i =0; i<9; i++){
			if(i!=y && plansza[x][i].zawartosc == cyfra)
				return true;
			if(i!=x && plansza[i][y].zawartosc == cyfra)
				return true;
			if(sektory[sektor][i] != plansza[x][y] && sektory[sektor][i].zawartosc == cyfra)
				return true;
		}
		return false;
	}

	Boolean czyNieprawidlowe(int x, int y, int cyfra) {
		int sektor = plansza[x][y].sektor;
		for(int i =0; i<9; i++){
			if(i!=y && plansza[x][i].zawartosc == cyfra)
				return true;
			if(i!=x && plansza[i][y].zawartosc == cyfra)
				return true;
			if(sektory[sektor][i] != plansza[x][y] && sektory[sektor][i].zawartosc == cyfra)
				return true;
		}
		return false;
	}

	int dajCyfre(int x, int y) {
		return plansza[x][y].zawartosc;
	}

// ------------------------- NOWY RUCH ---------------------------------------
// Jesli nowy ruch byl poprzedony cofnieciem to usuwa ogon historii.
// Umieszcza cyfre na planszy,
// dodaje wykonany ruch do historii

	void nowyRuch(int x, int y, int nowaCyfra) {
		if(cofniecia != 0) {
			usunKoniecHistorii();
		}
		historia.add(new Ruch(x, y, dajCyfre(x,y), nowaCyfra) );
		plansza[x][y].zawartosc = nowaCyfra;
	}

	void usunKoniecHistorii() {
		do{
			historia.remove(historia.size()-1);
			cofniecia--;
		} while(cofniecia != 0);
	}

// ------------------------- COFNIJ RUCH ----------------------------------------
//  Pobiera poprzednia wartosc pola z cofanego ruchu,
//  ale nie usuwa od razu historii, by umozliwic powtorzenie ruchu

	Ruch cofnijRuch() {
		Ruch tmp = null;
		if (historia.size()-cofniecia > 0) {
			cofniecia++;
			tmp = this.historia.get(historia.size()-cofniecia);
			plansza[tmp.x][tmp.y].zawartosc = tmp.poprzedniaWartosc;
		}
		return tmp;
	}

	Boolean moznaCofnac() {
		if (historia.size()-cofniecia > 0)
			return true;
		else
			return false;
	}

// ------------------------- POWTORZ RUCH --------------------------------------

	Ruch powtorzRuch() {
		Ruch tmp = null;
		if(cofniecia > 0) {
			tmp = this.historia.get(historia.size()-cofniecia);
			plansza[tmp.x][tmp.y].zawartosc = tmp.nowaWartosc;
			cofniecia--;
		}
		return tmp;
	}

// ------------------------- WCZYTANIE POMOCY -------------------------------------
//  Wczytuje plik pomoc.txt i przekazuje go do czesci graficznej

	String wczytajPomoc() {
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("pomoc.txt"));

			line = reader.readLine();
			while (line != null) {
				stringBuilder.append(line);
				stringBuilder.append(System.lineSeparator());
				line = reader.readLine();
		    }
			line =  stringBuilder.toString();
			reader.close();
		} catch (FileNotFoundException e) {
				System.out.println("Nie znaleziono pliku: pomoc.txt!");
				JOptionPane.showMessageDialog(new JFrame(),
				"Nie znaleziono pliku: pomoc.txt!", "Wczytywanie pomocy",
		        JOptionPane.ERROR_MESSAGE);}
		 catch (IOException e) {
				System.out.println("Wystąpił problem z wczytaniem pomocy!");
				JOptionPane.showMessageDialog(new JFrame(),
				"Wystąpił problem z wczytaniem pomoc!", "Wczytywanie pomocy",
		        JOptionPane.ERROR_MESSAGE);}
		return line;
	}

	String toString(int x, int y) {
		int cyfra = plansza[x][y].zawartosc;
		if (cyfra == 0){
			return "";
		}
		return Integer.toString(cyfra);
	}
	void drukujHistorie(){
		System.out.println("\nx  |  y  |  pop  |   nowy");
		Iterator<Ruch> it = historia.iterator();
		while (it.hasNext()){
			System.out.println(it.next());
		}
	}
	void drukujPlansze(){
		for(int x =0; x<9; x++){
			for(int y=0; y<9; y++){
				System.out.print(plansza[x][y].zawartosc);
			}
		System.out.println();
		}
		System.out.println();
	}
}

// ------------ KLASY POMOCNICZE: POLE I RUCH --------------------

class Pole implements Serializable{
	int zawartosc, sektor;
	Pole(int zawartosc, int sektor){
		this.zawartosc = zawartosc;
		this.sektor = sektor;
	}
}
class Ruch implements Serializable{
	int x, y;
	int poprzedniaWartosc, nowaWartosc;
	Ruch(){};
	Ruch(int x, int y, int poprzednia, int nowa) {
		this.x = x;
		this.y = y;
		this.poprzedniaWartosc = poprzednia;
		this.nowaWartosc = nowa;
	}
	public String toString() {
		return x+"  |  "+y+"  |  "+poprzedniaWartosc+"  |   "+nowaWartosc;
	}
}

// ----------------- ZAPIS I ODCZYT ROZGRYWKI -------------------
//  Zapisuje i wczytuje caly obiekt klasy Gra
//  czyli baze, aktualna plansze oraz historie ruchow

class Zapisy {
	Zapisy() {}

	void nowy(String nazwa, Gra gra) {

			File ff = new File(nazwa);
			if (ff.exists()) {
				String[] opcje = {"Tak", "Nie"};
				int n = JOptionPane.showOptionDialog(null,
					"Taki zapis juz istnieje!\nCzy chcesz go nadpisać?",
					"Nadpisanie gry",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, opcje, opcje[1]);
				if(n == 1) {
					return;
				}
			}
		try{
			FileOutputStream f = new FileOutputStream(nazwa);
			ObjectOutputStream outStr = new ObjectOutputStream(f);
			outStr.writeObject(gra);
			f.close();
		}catch (IOException e){
			System.out.println("Blad zapisu gry!");
			JOptionPane.showMessageDialog(new JFrame(),
					"Wystąpił problem z zapisem gry!", "Zapis gry",
			        JOptionPane.ERROR_MESSAGE);}
		System.out.println("Zapisano gre!");
		JOptionPane.showMessageDialog(null, "Zapisano grę!");
	}

	Gra wczytaj(String nazwa) {
		Gra gra = null;
		try{
			ObjectInputStream inStr = new ObjectInputStream(
							new FileInputStream(nazwa));

			gra = (Gra) inStr.readObject();

			inStr.close();
		}catch (FileNotFoundException fnf) {
				System.out.println("Zapis o takiej nazwie nie istnieje!");
				JOptionPane.showMessageDialog(new JFrame(),
				"Zapis o takiej nazwie nie istnieje!", "Wczytywanie gry",
		        JOptionPane.ERROR_MESSAGE);}
		catch (IOException e){
				System.out.println("Wystąpił problem z wczytaniem gry!");
				JOptionPane.showMessageDialog(new JFrame(),
				"Wystąpił problem z wczytaniem gry!", "Wczytywanie gry",
		        JOptionPane.ERROR_MESSAGE);
				gra = new Gra(); }
		catch (ClassNotFoundException e){}
		return gra;
	}
}


//------------------------  MAIN  ---------------------------

class GraMain {
	public static void main (String []arg) {
		GraGI graGI = new GraGI();
	}
}
