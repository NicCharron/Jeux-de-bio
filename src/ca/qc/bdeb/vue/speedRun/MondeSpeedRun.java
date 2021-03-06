//Toutes les methodes QUI NE SONT PAS DE SIMPLES GETTER ont une javadoc
package ca.qc.bdeb.vue.speedRun;

import ca.qc.bdeb.controleur.Controleur;
import ca.qc.bdeb.modele.Jeu;
import ca.qc.bdeb.modele.Modele;
import ca.qc.bdeb.vue.principale.FenetreJeu;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Le jeu Speed Run
 *
 * @author Batikan
 */
public class MondeSpeedRun extends JComponent {

    private Controleur controleur;
    private FenetreJeu fenetre;

    private JLabel lblQuestion;
    private JLabel lblTimer;
    private JTextField txtReponse;

    private Bot bot;

    private Joueur joueur;

    private ArrayList<String> listeQuestions = new ArrayList<>();
    private ArrayList<String> listeReponses = new ArrayList<>();

    private final int largeur = 800, hauteur = 600;

    private final int compteurReset = 26;
    private int timer = 0, compteur = compteurReset;

    private boolean finJeu = false;
    private boolean peutRepondre = false;

    private int[] index;

    private int nombreQuestionsRepondus = 0;

    private ProgressBar progressBar;

    Thread thread = new Thread() {
        @Override
        public void run() {
            super.run(); //To change body of generated methods, choose Tools | Templates.
            randomOrdre();
            while (!finJeu) {
                timer();
                finTour();

                invalidate();
                repaint();

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            finJeu();
            fenetre.fermerFenetre();
        }
    };

    public MondeSpeedRun(JLabel lblQuestion, JTextField txtReponse, JLabel lblTimer, FenetreJeu fenetre, Controleur controleur, Modele modele) {
        this.setPreferredSize(new Dimension(largeur, hauteur));
        this.setLayout(null);

        this.controleur = controleur;
        this.fenetre = fenetre;

        this.lblQuestion = lblQuestion;
        this.lblTimer = lblTimer;
        this.txtReponse = txtReponse;

        this.creerInterface();
        this.creerEvenements();

        this.thread.start();
    }

    /**
     * Cree l'interface graphique
     */
    private void creerInterface() {
        listeQuestions = controleur.getQuestionsSpeedRun(fenetre.getNiveauID());
        listeReponses = controleur.getReponsesSpeedRun(fenetre.getNiveauID());

        bot = new Bot(controleur);
        bot.setLocation(largeur - bot.getWidth() - 75, hauteur - bot.getHeight() - 50);
        this.add(bot);

        joueur = new Joueur(controleur);
        joueur.setLocation(75, hauteur - joueur.getHeight() - 50);
        this.add(joueur);

        lblTimer.setText(compteur + "");
        lblTimer.setSize(20, 20);
        lblTimer.setLocation((largeur - lblTimer.getWidth()) / 2, 100);
        this.add(lblTimer);

        lblQuestion.setSize(700, 100);
        lblQuestion.setLocation(50, 100);
        this.add(lblQuestion);

        txtReponse.setSize(700, 25);
        txtReponse.setLocation(50, 400);
        this.add(txtReponse);

        txtReponse.setEditable(peutRepondre);

        progressBar = new ProgressBar(listeQuestions);
        progressBar.setLocation((this.largeur - progressBar.getLargeur()) / 2, 50);
        this.add(progressBar);
    }

    /**
     * Cree les evenements
     */
    private void creerEvenements() {
        txtReponse.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e); //To change body of generated methods, choose Tools | Templates. 
                switch (e.getKeyChar()) {
                    case '\n':
                        if (peutRepondre) {
                            peutRepondre = false;
                            txtReponse.setEditable(peutRepondre);
                            verifierReponse();
                            txtReponse.setText("");
                            lblQuestion.setText("");
                            lblTimer.setText("");
                            compteur = compteurReset;
                        }
                }
            }

        });
    }

    /**
     * Determine une ordre quelconque
     */
    private void randomOrdre() {
        index = new int[listeQuestions.size()];

        Random random = new Random();

        for (int i = 0; i < listeQuestions.size(); i++) {
            index[i] = i;
        }

        int index1, index2, temporaire;

        for (int i = 0; i < 10; i++) {
            index1 = random.nextInt(listeQuestions.size());
            index2 = random.nextInt(listeQuestions.size());
            temporaire = index[index1];
            index[index1] = index[index2];
            index[index2] = temporaire;
        }

    }

    /**
     * Affiche la question
     */
    private void choixQuestion() {
        if (progressBar.getProgres() < index.length) {
            if (peutRepondre) {
                lblQuestion.setText(listeQuestions.get(index[progressBar.getProgres()]));
            }
        }
    }

    /**
     * Update le timer
     */
    private void timer() {
        if (timer % 100 == 0) {
            switch (compteur) {
                case 26:
                case 25:
                    lblTimer.setText("");
                    break;
                case 24:
                case 23:
                case 22:
                    lblTimer.setText((compteur - 21) + "");
                    break;
                case 21:
                    lblTimer.setText("");
                    break;
                case 20:
                    lblTimer.setText(compteur + "");
                    peutRepondre = true;
                    txtReponse.setEditable(peutRepondre);
                    choixQuestion();
                    break;
                case 0:
                    verifierReponse();
                    lblQuestion.setText("");
                    lblTimer.setText("0");
                    compteur = compteurReset;
                    peutRepondre = false;
                    txtReponse.setEditable(peutRepondre);
                    break;
                default:
                    lblTimer.setText(compteur + "");
                    break;

            }
            compteur--;
        }
        timer++;
    }

    /**
     * Verifie si la reponse est correcte
     */
    private void verifierReponse() {
        if (txtReponse.getText().toLowerCase().equals(listeReponses.get(index[progressBar.getProgres()]).toLowerCase())) {
            bot.enleverPointDeVie();
        } else {
            joueur.mauvaiseReponse();
        }

        progressBar.ajouterProgres();
    }

    /**
     * Fin des tours
     */
    private void finTour() {
        if (bot.botDetruit()) {
            this.remove(bot);
            bot = new Bot(controleur);
            bot.setLocation(largeur - bot.getWidth() - 75, hauteur - bot.getHeight() - 50);
            this.add(bot);
            joueur.botElimine();
        } else if (joueur.joueurDetruit() || !(progressBar.getProgres() < index.length)) {
            finJeu = true;
        }
    }

    /**
     * Fin du jeu
     */
    private void finJeu() {
        controleur.calculerScoreSpeedRun(fenetre.getNiveauID(), joueur.getScore());
        JOptionPane.showMessageDialog(this, "Votre score est " + controleur.getScoreNiveau(Jeu.SPEED_RUN, fenetre.getNiveauID()) + " points.");
    }

}
