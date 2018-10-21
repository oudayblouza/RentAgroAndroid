/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONException;
import org.json.JSONObject;

import Entities.Ville;
import Services.Interfaces.ICategorieVille;
import Util.AppSingleton;
import Util.Constants;



import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author oudayblouza
 */
public class VilleService implements ICategorieVille<Ville, Integer, String> {

    ProgressDialog progressDialog ;
    private static final String TAG = "MainActivity";
    List<Ville> listVilles = new ArrayList();
    List<Ville> lstVille = new ArrayList();
    Ville vil = new Ville();

    @Override
    public void add(Ville t) {
    }

    @Override
    public void delete(Integer id) {
    }

    @Override
    public Ville findById(Integer id) {


        return vil;
    }

    @Override
    public Ville findByNom(String nom) {


        return vil;
    }







    @Override

    public List<Ville> getAll(final Context context) {
        System.out.println("ccqdmck,mqkc,mk,fcmk,cmld,clm,fclmkq,cmlk,qfclmkq,dcmk,dqmc,dkc,dmfc,qdcfd");
       new VillesAsyncTask().execute(context);
        System.out.println(listVilles.get(1));
        return listVilles;

    }






    public ArrayList<Ville> getListVille(String json) {
        ArrayList<Ville> listVille = new ArrayList<Ville>();


        return listVille;

    }

    public Ville getOneVille(String json) {
        Ville e = new Ville();

        return e;

    }









public class VillesAsyncTask extends AsyncTask< Context, Void , List<Ville>> {


    @Override
    protected List<Ville> doInBackground(Context... contexts) {

        String url = Constants.ROOT_URL+"rentagro/ville/select.php";
        String  REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());


                        try {
                            Ville ville = new Ville();
                            for (int i = 0; i < response.getJSONArray("ville").length(); i++) {
                                JSONObject v = (JSONObject) response.getJSONArray("ville").get(i);
                                ville.setId(v.getInt("id"));
                                ville.setNom(v.getString("nom"));
                                lstVille.add(ville);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();


                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(contexts[0]).addToRequestQueue(jsonObjectReq,REQUEST_TAG);

        return lstVille;
}

}}
