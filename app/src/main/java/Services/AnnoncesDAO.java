package Services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.listAnnoncesAdapters;
import Entities.Annonces;
import Entities.Categorie;
import Entities.Delegation;
import Entities.Type;
import Entities.Users;
import Util.AppSingleton;
import Util.Constants;

import Util.VolleyCallback;

/**
 * Created by ouazseddik on 20/11/2017.
 */

public class AnnoncesDAO {
    public void getAnnonces(final List<Annonces> annoncesList, String response)
    {


        try {
            JSONObject object = new JSONObject(response);
            JSONArray Jarray  = object.getJSONArray("annonce");

            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject jsonObject = Jarray.getJSONObject(i);
                Annonces annonces = new Annonces(jsonObject);


                Delegation del = new Delegation();
                del.setId(jsonObject.getInt("delegation_id"));
                annonces.setDelegation(del);

                Type type = new Type();
                type.setId(jsonObject.getInt("type_id"));
                annonces.setType(type);

                Users user = new Users();
                user.setId(jsonObject.getInt("user_id"));
                annonces.setUser(user);

                annoncesList.add(annonces);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void executeGetAnnonces(Categorie categorie,final ArrayList<Annonces> annoncesList, final listAnnoncesAdapters adapter, Context context, final VolleyCallback volleyCallback)
    {        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";
        String url = Constants.ROOT_URL+"rentagro/annonces/getAnnonceByCateg.php?id="+categorie.getId();
        System.out.println(url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                volleyCallback.onSuccessResponse(response);
                System.out.println(response);

                adapter.notifyDataSetChanged();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
                //pd.dismiss();
            }
        });
        AppSingleton.getInstance(context).addToRequestQueue(postRequest,REQUEST_TAG);
    }



}
