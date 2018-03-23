/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.qc.bdeb.modele;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Batikan
 */
public class Groupe {

    private Modele modele;

    private String information;

    private ArrayList<Etudiant> listeEtudiants = new ArrayList<>();

    public Groupe(String information, Modele modele) {
        this.information = information;
        this.modele = modele;
        lectureInformation();
    }

    private void lectureInformation() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(information));
            String ligne = bufferedReader.readLine();
            while (ligne != null) {
                for (int i = 0; i < modele.getListeEtudiantSize(); i++) {
                    if (modele.getEtudiantChoisi(i).getDa().equals(ligne)) {
                        listeEtudiants.add(modele.getEtudiantChoisi(i));
                    }
                }

                ligne = bufferedReader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Groupe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Groupe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}