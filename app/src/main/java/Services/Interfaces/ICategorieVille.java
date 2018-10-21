/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services.Interfaces;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.List;


/**
 *
 * @author Le Parrain
 */
public interface ICategorieVille<O,I,S> {
     
    void add(O t);

    void delete(I id);

    List<O> getAll(Context context);

    O findById(I id);
    O findByNom(S nom);
}
