package info.androidhive.firebase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by choiwaiyiu on 18/3/2017.
 */

public class AvailableFriendAdapter extends BaseAdapter {
    Context context;
    List<User> rowItems;
    List<String> invitations;
    List<String> ids;

    private DatabaseReference mDatabase;
    private DatabaseReference mUserRef;
    private FirebaseAuth auth;
    private String myUID;



    public AvailableFriendAdapter(Context context, List<User> items, List<String> invitations, List<String> ids) {
        this.context = context;
        this.rowItems = items;
        this.invitations = invitations;
        this.ids = ids;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtName;
        Button inviteButton;
        Button acceptButton;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final AvailableFriendAdapter.ViewHolder holder;
        final User rowItem = (User) getItem(position);
        final String UID = ids.get(position);

        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserRef = mDatabase.child("users").child(UID);

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_availablefriend, null);
            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.listFriendName);
            holder.imageView = (ImageView) convertView.findViewById(R.id.listFriendIcon);
            holder.acceptButton = (Button) convertView.findViewById(R.id.button_accept);
            holder.inviteButton = (Button) convertView.findViewById(R.id.button_invite);

            convertView.setTag(holder);
        }
        else {
            holder = (AvailableFriendAdapter.ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(rowItem.getUsername());
        holder.imageView.setImageResource(R.drawable.logo); //
        //final ViewHolder finalHolder = holder;
        holder.inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //context.startActivity(new Intent(context, ConfirmationActivity.class));
                //Toast.makeText(context, finalHolder.txtName.getText().toString() , Toast.LENGTH_SHORT).show();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                String strDate = sdf.format(now);
                mDatabase.child("users").child(UID).child("invitations").child(myUID).setValue(strDate);
                mDatabase.child("users").child(UID).child("notifications").child("invitations").child(myUID).child("time").setValue(strDate);
                mDatabase.child("users").child(UID).child("notifications").child("invitations").child(myUID).child("username").setValue(Me.myName);

                Snackbar.make(v, "You have sent an invitation!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

            }
        });

        //see if there is invitation


        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ConfirmationActivity.class));
            }
        });

        mUserRef.child("invitations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(invitations.contains(UID)){
                    holder.inviteButton.setVisibility(View.GONE);
                    holder.acceptButton.setVisibility(View.VISIBLE);
                }
                else{
                    holder.inviteButton.setVisibility(View.VISIBLE);
                    holder.acceptButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }
}