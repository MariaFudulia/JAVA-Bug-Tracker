Fudulia Maria

Acest proiect implementeaza un sistem de gestionare al ciclului de viata al
dezvoltarii software, acoperind raportarea bug-urilor, cererile de 
functionalitati noi si feedback-ul UI. Sistemul gestioneaza ierarhii de
utilizatori cu permisiuni diferite, fluxuri automate de lucru si generarea 
de rapoarte analitice complexe.

Structura pachetelor si ierarhiile de clase
Arhitectura este organizata in pachete pentru a asigura separarea responsabilitatilor.

Pachetul users contine utilizatorii sistemului, organizati intr-o ierarhie bazata pe
clasa abstracta User.
Developer gestioneaza expertiza, semioritatea si tichetele asignate. Aceasta clasa
implementeaza Observer pentru a gestiona notificarile.
Manager administreaza subordonatii si creeaza milestone-uri.
Reporterul este responsabil pentru raportarea tichetelor.

Pachetul commands incapsuleaza actiunile utilizatorilor, folosind clasa abstracta Command.

Pachetul services contine logica pentru calculul metricilor(performanta, risc, impact, stabilitate).

Pachetul milestones gestioneaza gestioneaza etapele proiectului, monitorizand progresul si
dependentele dintre ele.

Pachetul database este un strat de persistenta in memorie pentru utilizatori, tichete si
milestone-uri.

Sistemul functioneaza pe baza unei arhitecturi command-driven, orchestrata prin obiectul central
AppContext.

Fluxul de executie:
1. clasa App incarca datele despre utilizatori prin UserLoader si intrarile prin InputLoader.
2. pentru fiecare comanda, sunt actualizate atat tranzitiile automate intre fazele worflow-ului,
cat si starile milestone-urilor.
3. CommandFactory instantiaza obiectul Command corespunzator, care valideaza permisiunile 
utilizatorului si executa operatii asupra bazelor de date.
4. MilestoenService recalculeaza prioriatile tichetelor si verifica daca finalizarea unor tichete
5. duce la deblocarea altor milestone-uri.

Design Patterns utilizate:
1.Factory Method Pattern(atat pentru UserFactory cat si pentru CommandFactory)
Sistemul utilizeaza fabrici pentru a gestiona instantierea obiectelor complexe. UserFactory incapsuleaza
logica de creare a tipurilor specifice de utilizatori pe baza rolului din JSON, in timp ce CommandFactory
creeaza dinamic obiecte de comanda pe baza numelui instructiunii primite.

2.Command Pattern(pentru ierarhia de comenzi)
Prin transformarea comenzilor în obiecte(ex: AssignTicketCommand, AddCommentCommand),
sistemul separa obiectul care invoca actiunea de cel care o executa. Acest lucru permite o validare
simpla a permisiunilor si o extensibilitate facila pentru noi tipuri de actiuni.

3.Observer Pattern(pentru gestionarea notificarilor)
Clasa Milestone actioneaza ca un Subject care notifica obiectele Observer (dezvoltatorii asignati)
despre evenimente critice, cum ar fi apropierea deadline-ului sau deblocarea unei etape de care 
acestia depind.

4.State Pattern (WorkflowPhase)
Comportamentul aplicatiei se schimba dinamic în funcție de faza curenta. De exemplu, în TestingPhase
este permisă raportarea tichetelor, în timp ce în DevelopmentPhase accentul se muta pe rezolvare.
Această logica este delegata implementărilor interfeței Workflow, evitand structurile de tip if-else
masive.

5.Builder Pattern(TicketBuilder si MilestoneBuilder)
Am utilizat acest pattern pentru a construi tichete pas cu pas. Deoarece un tichet poate fi de tip BUG,
FEATURE_REQUEST sau UI_FEEDBACK, fiecare avand campuri specifice, builder-ul permite setarea doar a 
atributelor necesare pentru tipul respectiv inainte de apelarea metodei build().

Milestone-urile au o structură complexa ce include liste de tichete (tickets),
dezvoltatori asignați (assignedDevs) și milestone-uri blocate (blockingFor). Builder-ul permite
configurarea acestor liste intr-o maniera curata în clasa MilestoneService.

Gestionarea Milestone-urilor:
Monitorizarea Progresului: Milestone-ul calculeaza completionPercentage raportand numarul de tichete 
cu statusul CLOSED la totalul de tichete asociate.
Calculul Termenelor: Campurile daysUntilDue și overdueBy sunt actualizate dinamic de catre MilestoneService
folosind ChronoUnit.DAYS intre data curenta și dueDate.
Prioritizarea Automata: Dacă un milestone nu este blocat, MilestoneService creste prioritatea tichetelor 
deschise la fiecare 3 zile sau le marcheaza ca CRITICAL daca deadline-ul este aproape.