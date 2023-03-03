package com.oliviabecht.civiladvocacy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<Office> officeList;
    private Context context;
    private Picasso picassoInstance;

    public Adapter(ArrayList<Office> officeList, Context context, Picasso picasso){
        this.officeList = officeList;
        this.context = context;
        this.picassoInstance = picasso;

    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //this method creates what will be shown in this recycler view and defines all action for button clicks
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_item_main, parent, false);
        //call the view holder class to assign xml to java elements
        final ViewHolder holder = new ViewHolder(v);
        return holder;

    }

    @Override
    //this actually puts the data where it is supposed to go
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txtOffice.setText(officeList.get(position).getJobTitle());
        holder.txtName.setText(officeList.get(position).getName());
        holder.txtParty.setText(officeList.get(position).getParty());
        //holder.imageOfficial.setImage

        //Need to handle the case they do not have an image
            //Picasso..load(allGovOfficials.get(position).getPhotoURL()).into(holder.imageOfficial);
            //TODO need to fix this not currently working
        String url = officeList.get(position).getPhoto();

        picassoInstance.load(url).placeholder(R.drawable.missing).error(R.drawable.brokenimage).resize(116, 128).centerCrop().into(holder.imageOfficial);

        //USER CLICKS A ROW -> GO TO OFFICIAL ACTIVITY
        holder.row.setOnClickListener(view -> {
            Intent officialIntent = new Intent(view.getContext(), OfficialActivity.class);
            officialIntent.putExtra("Office", officeList.get(position));
            //officialIntent.putExtra("existingGovOfficial", officeList);
            //officialIntent.putExtra("positionOfOffice", position);

            //Downloader.getData(MainActivity.this, zipCode, allGovOfficials, adpCivil);

            view.getContext().startActivity(officialIntent);
            //activityResultLauncher.launch(officialIntent);
        });

        //ClickARow(holder, position);



    }

    public void ClickARow(ViewHolder holder, int position) {
        holder.row.setOnClickListener(view -> {
            Intent officialIntent = new Intent(view.getContext(), OfficialActivity.class);
            officialIntent.putExtra("Office", (CharSequence) officeList.get(position));
            officialIntent.putExtra("existingOfficeList", officeList);
            officialIntent.putExtra("positionInOfficeList", position);

            //Downloader.getData(MainActivity.this, zipCode, allGovOfficials, adpCivil);

            view.getContext().startActivity(officialIntent);
            //activityResultLauncher.launch(officialIntent);
        });
    }

    /**
     * After deleting note, rewrite notes.json
     */
    /*
    private void saveRestOfNotes(Context context){
        try {
            File file = new File(context.getFilesDir(), MainActivity.FILENAME);
            //check to see if this file exists
            String filesDir = context.getFilesDir().getAbsolutePath();
            if (!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = context.openFileOutput(MainActivity.FILENAME, Context.MODE_PRIVATE);

            JSONArray notesJSONArray = new JSONArray();

            for(int i = 0; i < allNotes.size(); i++) {
                JSONObject newNoteObj = new JSONObject();
                newNoteObj.put("title", allNotes.get(i).getTitle());
                newNoteObj.put("text", allNotes.get(i).getText());
                newNoteObj.put("date", allNotes.get(i).getDate());
                notesJSONArray.put(newNoteObj);
            }
            fos.write(notesJSONArray.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            Log.d("Write ERROR", "Error: could not write to note file");
            e.printStackTrace();
        }
        Log.d("Write Success", "good");
    }
    */


    public Office getItem(int i){
        return officeList.get(i);
    }

    @Override
    //*****REALLY IMPORTANT******
    //If this returns 0 it wont show any data. This returns how many row items should be shown
    //so it should always match the size of the data to show
    public int getItemCount() {
        return officeList.size();
    }

    //view holder class helps keep the previously created views in memory so they load faster
    //this should be all the xml elements in your layout and it should map them to their java variables
    public static class ViewHolder extends RecyclerView.ViewHolder{
        //This should represent all XML elements in one row of the recycler view
        TextView txtOffice,txtName,txtParty;
        ImageView imageOfficial;
        ConstraintLayout row;
        public ViewHolder(@NonNull View view) {
            super(view);

            //TODO ADD PHOTO for recView Row

            txtOffice = view.findViewById(R.id.RecItemJobName);
            txtName = view.findViewById(R.id.RecItemName);
            txtParty = view.findViewById(R.id.RecItemParty);
            imageOfficial = view.findViewById(R.id.RecItemOfficialPhoto);
            row = view.findViewById(R.id.RecItemCS);
        }
    }


}
