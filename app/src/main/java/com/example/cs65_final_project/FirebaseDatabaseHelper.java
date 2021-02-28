package com.example.cs65_final_project;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cs65_final_project.adapters.FridgeListViewAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class FirebaseDatabaseHelper {

    /**
     * Adds an ingredient to Firebase, incrementing the amount of the ingredient if it already exists
     * @param context
     * @param name
     * @param amount
     */
    public static void addIngredient(Context context, String name, float amount) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ref.child("users").child(auth.getUid()).child("fridge").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) { // Ingredient already exists in the database
                    float existingAmount = snapshot.getValue(Float.class);
                    ref.child("users").child(auth.getUid()).child("fridge").child(name).setValue(existingAmount+amount);
                    Log.d("RecipeFinderDatabase", "Added to Existing Ingredient");
                    ((AppCompatActivity)(context)).finish();
                } else { // Ingredient does not currently exist in the database
                    ref.child("users").child(auth.getUid()).child("fridge").child(name).setValue(amount);
                    Log.d("RecipeFinderDatabase", "Added new Ingredient");
                    ((AppCompatActivity)(context)).finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Gets all of the ingredients in a user's fridge
     * @param ingredients
     * @param fridgeListViewAdapter
     */
    public static void getIngredients(ArrayList<Ingredient> ingredients, FridgeListViewAdapter fridgeListViewAdapter) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        ref.child("users").child(auth.getUid()).child("fridge").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) { // Ensures that there is data to retrieve
                    for (DataSnapshot data : snapshot.getChildren()) { // Loops through each ingredient in the fridge
                        ingredients.add(new Ingredient(data.getKey(), data.getValue(Float.class)));
                    }
                    fridgeListViewAdapter.notifyDataSetChanged(); // Update the adapter to display all retrieved ingredients
                    Log.d("RecipeFinderDatabase", "Retrieved Fridge");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void loadProfile(EditText nameEditText, EditText bioEditText, EditText emailEditText) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameEditText.setText(snapshot.child("name").getValue(String.class));
                bioEditText.setText(snapshot.child("bio").getValue(String.class));
                emailEditText.setText(auth.getCurrentUser().getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void updateProfile(Context context, String name, String bio) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(auth.getUid()).child("name").setValue(name);
        ref.child("users").child(auth.getUid()).child("bio").setValue(bio).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show();
                ((AppCompatActivity)(context)).finish();
            }
        });
    }
}