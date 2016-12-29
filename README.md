O aplikácii Doc Iľko
===
Vytvorme aplikáciu na zistenie teploty v konkrétnom meste. Podporujme veľké zariadenia.

*	Pre veľké zariadenia zobrazme vľavo zoznam obcí a vpravo detailné informácie o teplote.
*	Pre malé zariadenia zobrazme zoznam obcí. Po výbere obce sa prepnime do detailu s teplotou


### Plán práce
1.	Vyrobíme verziu pre veľké zariadenia. Použijeme **statické fragmenty**
2.	Vyrobíme verziu pre malé zariadenia. Použijeme **dynamické fragmenty**.

Verzia pre veľké zariadenia
====
1.	Vyrobíme hlavnú aktivitu, ktorá nebude robiť nič.
2.	Vyrobíme postupne dva fragmenty: pre *master* (zoznam obcí) a *detail*.

## Master fragment
*	Necháme si vyrobiť **Fragment** `MasterFragment`. Nenecháme vygenerovať žiadne *fragment factory methods* ani *interface callbacks*, pretože nás popletú a aj tak k nim prídeme neskôr

### Layout
Layout súbor `fragment_master.xml` bude obsahovať len jeden `ListView`:

	<ListView
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:tools="http://schemas.android.com/tools"
	    tools:context="sk.upjs.docilko.MasterFragment"
	
	    android:id="@+id/cityListView"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:entries="@array/citiesArray"
	/>
	
Vlastnosti:

*	listview bude na celú šírku a výšku fragmentu
*	dostane identifikátor
*	položky vytiahneme zo statického zoznamu. Do `strings.xml` dodajme pevný zoznam obcí:
	
		    <array name="citiesArray">
		        <item>Košice</item>
		        <item>Prešov</item>
		        <item>Humenné</item>
		        <item>Michalovce</item>
		    </array>

### Kód
*	Fragment dedí od triedy `android.app.Fragment`. Android Studio môže nagenerovať kód, kde sa dedí od triedy z knižnice kompatibility `android.support.v4.app.Fragment`. V súčasnosti však už nemusíme podporovať Android 2.x, kde musíme fragmenty emulovať..
*	Fragment musí mať prázdny verejný konštruktor!
	*	v Jave: ak trieda nemá uvedený žiadny konštruktor, kompilátor "domyslí" prázdny verejný konštruktor
*	strom widgetov sa z layoutového XML vytvorí v metóde `onCreateView()`.
*	na rozdiel od metódy `onCreate()` v aktivite musí metóda vo fragmente vracať výsledný objekt pre `View`

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        return inflater.inflate(R.layout.fragment_master, container, false);
	    }

## Detailový fragment

### Layout

Layoutový súbor `fragment_detail.xml` bude obsahovať jeden vycentrovaný veľký `TextView`.

	<TextView
	    xmlns:android="http://schemas.android.com/apk/res/android"
	    xmlns:tools="http://schemas.android.com/tools"
	    tools:context="sk.upjs.docilko.DetailFragment"
	
	    android:id="@+id/temperatureTextView"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:gravity="center"
	    android:text="neznáma teplota"
	    android:textSize="36dp"
	    />
	    
### Kód

Podobne ako v prípade master fragmentu, začneme nafúknutím layoutu z XML a jeho navrátením v tvare `View` objektu

## Hlavná aktivita pre veľké zariadenia

Veľké zariadenia budú mať vlastný layout pre aktivitu. Vytvorme samostatný layout pre hlavnú aktivitu: **File | New | XML | Layout XML File** a nastavme kvalifikátory:

*	rozloženie *landscape*
*	šírka zariadenia aspoň 600 dp (zodpovedá 7 palcovému zariadeniu).

Vznikne layoutovací súbor s kvalifikátorom `w600dp-land`.

### Fragmenty

V layoute aktivity pre dve zariadenia umiestnime dva statické fragmenty do lineárneho layoutu, ktorému nastavíme horizontálnu orientáciu. Fragmenty sa tak umiestnia vedľa seba.

	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    android:orientation="horizontal" android:layout_width="match_parent"
	    android:layout_height="match_parent">
	
	
	    <fragment
	        android:id="@+id/masterFragment"
	        class="sk.upjs.docilko.MasterFragment"
	        android:layout_width="0dp"
	        android:layout_height="match_parent"
	        android:layout_weight="1" />
	
	    <fragment
	        android:id="@+id/detailFragment"
	        class="sk.upjs.docilko.DetailFragment"
	        android:layout_width="0dp"
	        android:layout_height="match_parent"
	        android:layout_weight="2" />
	</LinearLayout>

### Kód aktivity
	
	public class MainActivity extends AppCompatActivity {
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        }
	    }
	}

### Spustenie aplikácie

Po spustení aplikácie na veľkom zariadení v horizontálnom móde uvidíme dva fragmenty vedľa seba.

Koordinácia fragmentov
----------------------

Dohodnime komunikáciu a koordináciu medzi fragmentami:

*	po kliknutí na zoznam obcí v master fragmente musíme zistiť vybranú obec
*	tú musíme dopraviť do detailového fragmentu, ktorý zobrazí informácie

### Poslucháči cez interfejs

Komunikáciu vyriešme cez spoločný interfejs, a vysielača a prijímače.

*	dohodneme protokol komunikácie v podobe interfejsu `OnCityClickListener` s metódou `void onCityClicked(String city)`.
*	master fragment bude mať zaregistrovaného poslucháča tohto typu, do ktorého bude posielať vybrané mesto po jeho zvolení v zozname `ListView`
*	aktivita bude poslucháčom na zmeny v master fragmente. Vybranú obec bude posielať do detailového fragmentu.

#### Úprava master fragmentu

* Do master fragmentu dáme inštanciu poslucháča, ktorému budeme posielať zmeny
* listviewu pridelíme poslucháča na výber položky, ktorý len vyberie zo zoznamu reťazec z vybranou obcou a prepošle ju poslucháčovi na vybranú obec

		public class MasterFragment extends Fragment {
		
		    private OnCityClickListener onCityClickListener;
		
		    public MasterFragment() {
		        // Required empty public constructor
		    }
		
		    @Override
		    public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                             Bundle savedInstanceState) {
		
		
		        View view = inflater.inflate(R.layout.fragment_master, container, false);
		        ListView cityListView = (ListView) view.findViewById(R.id.cityListView);
		        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		                onCityListViewItemClickListener(adapterView, view, i, l);
		            }
		        });
		
		        return view;
		    }
		
		    private void onCityListViewItemClickListener(AdapterView<?> adapterView, View view, int i, long l) {
		        String city = (String) adapterView.getAdapter().getItem(i);
		        onCityClickListener.onCityClicked(city);
		    }
		
		    public void setOnCityClickListener(OnCityClickListener onCityClickListener) {
		        this.onCityClickListener = onCityClickListener;
		    }
		}
		
#### Úprava aktivity

*	Aktivita bude počúvať na výber obcí v hlavnom fragmente
	*	necháme ju implementovať `OnCityClickListener`
	*	potrebujeme ju zaregistrovať v master fragmente ako poslucháča
*	získanie objektu fragmentu sa vykonáva pomocou **Fragment Manager**a, ktorý spravuje všetky fragmenty v aktivite
	*	získame ho cez `getFragmentManager()`.
	*	pozor! fragment nie je *view*, preto ho nezískavame cez `findViewById()`!

Výsledok:

	public class MainActivity ... implements OnCityClickListener {
	
		...
		
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	    	...
            MasterFragment masterFragment = (MasterFragment) getFragmentManager().findFragmentById(R.id.masterFragment);
            masterFragment.setOnCityClickListener(this);
	    }
	
	    @Override
	    public void onCityClicked(String city) {

	    }
	
	}

### Prenos informácií do detailu

*	detailový fragment je objekt, môže mať svoj setter

		public class DetailFragment extends Fragment {
			...
		
		    public void setTemperature(int temperature) {
		        TextView textView = (TextView) getView().findViewById(R.id.temperatureTextView);
		        textView.setText(Integer.toString(temperature));
		    }
			...
		}


	
### Prenos informácií z aktivity do detailu

*	upravíme metódu reagujúcu na výber obce
*	získame inštanciu detailového fragmentu a nastavíme jej aktuálnu teplotu
*	teplotu vypočítame *Mimoriadne Zlým Algoritmom* (MZA(tm))

		public class MainActivity extends AppCompatActivity implements OnCityClickListener {
				
		    @Override
		    public void onCityClicked(String city) {
	            DetailFragment detailFragment = (DetailFragment)
	            		getFragmentManager().findFragmentById(R.id.detailFragment);
	            detailFragment.setTemperature(getTemperatureFromCity(city));
		        }
		    }
		
		    private int getTemperatureFromCity(String city) {
		        return -city.hashCode();
		    }
		}
		    
		    
Verzia pre malé zariadenia
=======
Malé zariadenia budú dynamicky prepínať dva fragmenty: detailový a masterový.

*	Otvoríme layout pre malé zariadenia (ide o štandardný layout hlavnej aktivity)
*	Layout bude prázdny, pretože jeho obsah bude tvoriť buď jeden alebo druhý fragment.
*	Dôležité je prideliť layoutu identifikátor `@+id/activity_main`
	
		<?xml version="1.0" encoding="utf-8"?>
		<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
		    xmlns:tools="http://schemas.android.com/tools"
		    android:id="@+id/activity_main"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent"
		    android:paddingBottom="@dimen/activity_vertical_margin"
		    android:paddingLeft="@dimen/activity_horizontal_margin"
		    android:paddingRight="@dimen/activity_horizontal_margin"
		    android:paddingTop="@dimen/activity_vertical_margin"
		    tools:context="sk.upjs.docilko.MainActivity"
		    >
		
			<!-- layout riesi fragment -->
		
		</RelativeLayout>
		
*	Identifikátor použijeme na odlíšenie režimu, v ktorom sa zariadenie nachádza:
	*	pre veľké zariadenia v landscape orientácii sa použijú dva fragmenty vedľa seba (layout bude bez IDčka)
	*	pre malé zariadenia bude layout s dynamickými fragmentami, bude s IDčkom
*	do aktivity dáme rozlišovaciu metódu, ktorá rozhodne, v akom režime sme:

	    private boolean isSinglePanelMode() {
	        return findViewById(R.id.activity_main) != null;
	    }
*	na základe tejto metódy sa rozhodneme, či zobrazíme dva statické fragmenty vedľa seba alebo budeme musieť dynamicky zamieňať fragment so zoznamom obcí za fragment s detailom a naopak.

Prepínanie módov
---------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isSinglePanelMode()) {
        	// *****************************
        	//
			// tu budeme nahrádzať fragmenty
			//
        	// *****************************				
        } else {
            MasterFragment masterFragment = (MasterFragment) getFragmentManager().findFragmentById(R.id.masterFragment);
            masterFragment.setOnCityClickListener(this);
        }
    }	


Správca fragmentov a transakcie
-----------------------
*	statické fragmenty uvádzané cez element `<fragment>` sú napevno "vyryté" a nedajú sa rozumne skrývať či zobrazovať
*	namiesto toho máme **dynamické fragmenty**, ktoré sa dajú nahrádzať, skrývať, či zobrazovať
*	spravovanie statických aj dynaických fragmentov má pod palcom **správca fragmentov** / **fragment manager**

### Transakcie
*	nahrádzanie fragmentov je zložitá operácia
	*	vyžaduje modifikáciu stromu widgetov
*	musí bežať v rámci **transakcie**
*	transakcia je z technického hľadiska postupnosť krokov, ktorá sa vykoná nad stromom widgetov
	*	transakcia musí prejsť celá
	*	ak sa nepodarí, musí sa odvolať
	*	výmena fragmentov musí prejsť celá alebo sa zrušiť, inak by sme mali nekonzistentný strom widgetov

### Zobrazenie master fragmentu v transakcii
*	Po štarte aktivity chceme zobraziť hlavný master fragment. 
*	Prázdny layout v aktivite nahradíme layoutom master fragmentu
*	Doplňme chýbajúci kód:

        if (isSinglePanelMode()) {
            MasterFragment masterFragment = new MasterFragment();
            masterFragment.setOnCityClickListener(this);

            getFragmentManager()
                    .beginTransaction()
                        .replace(R.id.activity_main, masterFragment)
                    .commit();
		}
		...
		
*	Pri vytváraní aktivity 
	1.	vytvoríme objekt master fragmentu
	2.	asociujeme aktivitu ako poslucháča na zmeny
	3.	získame správcu fragmentov
	4.	spustíme transakciu
	5.	nahradíme layout s ID `activity_main` objektom fragmentu
		*	toto ID existuje v layoute pre malé zariadenia, dali sme ho do XML
	6.	transakciu potvrdíme cez **commit**
	
	
### Zobrazenie detailového fragmentu v transakcii
*	Prepnutie do detailového fragmentu potrebuje niekoľko úprav.
	*	Fragment detailu potrebuje pri vytvorení vedieť teplotu, ktorú zobrazí.
	*	Pri statických fragmentoch sme to robili cez *setter* metódu
	*	V tomto prípade potrebujeme vedieť teplotu už pri vytváraní detailového fragmentu
	
#### Úprava detailového fragmentu

*	**Dôležité**: fragment musí mať implicitný konštruktor (`public` konštruktor bez parametrov)	
*	Kvôli správe životného cyklu nesmie mať iné konštruktory. Namiesto toho však vieme konštruktory s parametrami emulovať cez **statické továrenské metódy**.
*	Dodajme do `DetailFragment`u metódu:
	
	    public static DetailFragment newDetailFragment(int temperature) {
	        DetailFragment fragment = new DetailFragment();
	
	        Bundle arguments = new Bundle();
	        arguments.putInt("temperature", temperature);
	
	        fragment.setArguments(arguments);
	
	        return fragment;
	    }
	
*	Emuláciu parametrov v konštruktore dosiahneme cez **bundle** (prakticky hashmapa s ľubovoľnými dátami).
	*	Do bundlu vložíme hodnotu teploty
	*	Túto hodnotu si vyzdvihneme vo fragmente v metóde `onCreate()`.
*	Fragment si vie vypýtať argumenty (t. j. bundle), s ktorými bol zavolaný) cez metódu `getArguments()`. V metóde `onStart()` si ich vyzdvihneme a použijeme pri aktualizácii teploty.

	    @Override
	    public void onStart() {
	        super.onStart();
	
	        Bundle arguments = getArguments();
	        if(arguments != null) {
	            int temperature = arguments.getInt("temperature");
	            setTemperature(temperature);
	        }
	    }

### Úprava kliknutia
	
	
*	Prepíšeme metódu na obsluhu kliknutia na obec
*	Opäť overíme, či sme v jednopanelovom režime (pre malé zariadenia)
*	Vytvoríme inštanciu detailového fragmentu pomocou statickej továrenskej metódy
*	Spustíme transakciu
*	Nahradíme layout s ID `activity_main` objektom fragmentu
*	Transakciu commitneme.

	    @Override
	    public void onCityClicked(String city) {
	        if (isSinglePanelMode()) {
	            DetailFragment detailFragment = DetailFragment.newDetailFragment(getTemperatureFromCity(city));
	            getFragmentManager().beginTransaction()
	                    .replace(R.id.activity_main, detailFragment)
	                    .addToBackStack(NO_NAME)
	                    .commit();
	
	        } else {
	            DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.detailFragment);
	            detailFragment.setTemperature(getTemperatureFromCity(city));
	        }
	    }

## História a tlačidlo "Späť"

Ak sa prepneme z master fragmentu do detailového (výberom obce) a následne stlačíme tlačidlo "Späť", aplikácia sa ukončí.

Toto správanie nedáva zmysel, pretože omnoho lepšie by bolo vrátiť sa späť do zoznamu obcí, teda do masterového fragmentu.

Našťastie, Android podporuje pri fragmentoch históriu v podobe **back stack** (teda zásobníka fragmentov, ktorý funguje podobne ako zásobník aktivít).

Pri vytváraní transakcie stačí zavolať metódu `addToBackStack()` s parametrom `null`, čo zaručí, že výmena master fragmentu za detailový fragment bude rešpektovať históriu.

            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_main, detailFragment)
                    .addToBackStack(null)
                    .commit();
	

		
			    