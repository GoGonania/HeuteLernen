package de.keplerware.heutelernen.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import de.keplerware.heutelernen.Client;
import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.MyFragment;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Rang;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.manager.BildManager;
import de.keplerware.heutelernen.manager.ProfilManager;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MyText;

public class FragmentProfil extends MyFragment{
    public UserInfo info;
    private LinearLayout angebote;
    private ImageView bild;
    private boolean editP;
    private File image;
    private Uri imageUri;

    public static FragmentProfil show(UserInfo info){
        FragmentProfil f = new FragmentProfil();
        f.setArguments(ProfilManager.create(info));
        return f;
    }

    public void updatePic(boolean uc){
        BildManager.get(info.id, uc, bild, getActivity());
    }

    public View create(){
        if(getArguments() != null){
            info = ProfilManager.get(getArguments());
        } else{
            info = Sitzung.info;
        }
        View v = Screen.inflate(R.layout.profil);
        ((TextView) v.findViewById(R.id.profil_name)).setText(info.name);
        ((TextView) v.findViewById(R.id.profil_details)).setText(info.klasse+"\nWohnort: "+info.ort+"\nSchule: "+info.schuleText);
        final TextView tB = (TextView) v.findViewById(R.id.profil_beschreibung);
        if(info.beschreibung.isEmpty()){
            tB.setTypeface(null, Typeface.ITALIC);
            tB.setText("Keine Beschreibung");
        } else{
            tB.setText(info.beschreibung);
        }
        angebote = (LinearLayout) v.findViewById(R.id.profil_angebote);

        editP = Sitzung.rang(Rang.MODERATOR) || info.owner();

        ImageView editB = (ImageView) v.findViewById(R.id.profil_edit_beschreibung);

        editB.setVisibility(editP ? View.VISIBLE : View.GONE);

        bild = (ImageView) v.findViewById(R.id.profil_bild);

        bild.setLongClickable(true);
        bild.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                updatePic(false);
                return true;
            }
        });

        updatePic(true);

        if(editP){
            bild.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Profilbild bearbeiten");
                    builder.setItems(new String[]{"Bild hochladen", "Bild löschen"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which){
                            if(which == 0){
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Bild hochladen");
                                builder.setItems(new String[]{"Vorhandenes Bild auswählen", "Neues Bild aufnehmen"}, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which){
                                        if(image == null) {
                                            image = new File(Environment.getExternalStorageDirectory()+"/"+Util.appname+"", "last.jpg");
                                            if(!image.exists()) image.getParentFile().mkdirs();
                                            imageUri = Uri.fromFile(image);
                                        }
                                        if(which == 0){
                                            Intent intent = new Intent();
                                            intent.setType("image/*");
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                            try{
                                                intent.putExtra("return-data", true);
                                                startActivityForResult(Intent.createChooser(intent, "Bild hochladen"), 1);
                                            }catch (ActivityNotFoundException e){}

                                        } else{
                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                            try{
                                                image.delete();
                                                intent.putExtra("return-data", true);
                                                startActivityForResult(Intent.createChooser(intent, "Bild aufnehmen"), 2);
                                            }catch (ActivityNotFoundException e){}
                                        }
                                    }
                                });
                                builder.show();
                            } else{
                                new Thread(new Runnable(){
                                    public void run(){
                                        try{
                                            Util.toastUI("Bild wird gelöscht...\nBitte warten...");
                                            Client c = new Client();
                                            c.delete(info.id);
                                            c.close();
                                            updatePic(false);
                                            Util.toastUI("Bild wurde gelöscht!");
                                        }catch (IOException e){}
                                    }
                                }).start();
                            }
                        }
                    });
                    builder.show();
                }
            });

            editB.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    String b = info.beschreibung;
                    Dialog.prompt(b.isEmpty() ? "Wähle deine Beschreibung" : "Ändere deine Beschreibung", b, new Dialog.PromptListener(){
                        public void ok(String text){
                            if(text.isEmpty()) return;
                            if(text.length() > 100){
                                Util.toast("Deine Beschreibung wurde auf 100 Zeichen gekürzt");
                                text = text.substring(0, 100);
                            }
                            final String text2 = text;
                            Internet.beschreibung(info.id, text, new Util.Listener(){
                                public void ok(String data){
                                    info.beschreibung = text2;
                                    tB.setText(text2);
                                }

                                public void fail(Exception e){}
                            });
                        }
                    });
                }
            });
        }
        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == UCrop.REQUEST_CROP){
            if(resultCode == Activity.RESULT_OK){
                uploadUri(UCrop.getOutput(data));
            } else{
                if(resultCode == UCrop.RESULT_ERROR) UCrop.getError(data).printStackTrace();
                image.delete();
            }

        }

        if(resultCode != Activity.RESULT_OK) return;

        if(requestCode == 1) crop(data.getData());
        if(requestCode == 2) crop(imageUri);
    }

    private void crop(Uri d){
        UCrop u = UCrop.of(d, imageUri);
        UCrop.Options o = new UCrop.Options();
        o.withAspectRatio(1, 1);
        o.withMaxResultSize(100, 100);
        o.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        o.setToolbarTitle("Bild zuschneiden");
        o.setActiveWidgetColor(getResources().getColor(R.color.actionbar, null));
        o.setToolbarColor(getResources().getColor(R.color.actionbar, null));
        o.setStatusBarColor(getResources().getColor(R.color.actionbarDunkler, null));
        o.setShowCropGrid(false);
        u.withOptions(o);
        u.start(getActivity(), this);
    }

    private void uploadUri(final Uri s){
        new Thread(new Runnable(){
            public void run() {
                try {
                    Util.toastUI("Bild wird hochgeladen...\nBitte warten...");
                    Client c = new Client();
                    c.upload(info.id, info.id+".jpg", getContext().getContentResolver().openInputStream(s));
                    c.close();
                    Util.toastUI("Bild wurde hochgeladen!");
                    updatePic(false);
                } catch (Exception e){
                    Util.toastUI("Keine Internet-Verbindung!");
                    e.printStackTrace();
                }
                image.delete();
            }
        }).start();
    }

    public void update(){
        angebote.removeAllViews();
        load();
    }

    private void load(){
        Internet.angebote(info, new Internet.AngebotListener(){
            public void ok(Internet.Angebot[] as){
                angebote.removeAllViews();
                if(as == null){
                    angebote.addView(new MyText("Keine Nachhilfefächer gefunden!"));
                } else{
                    MyList<Internet.Angebot> liste = new MyList<Internet.Angebot>(as){
                        public View view(final Internet.Angebot angebot){
                            View v = Screen.inflate(R.layout.angebot);
                            ((TextView) v.findViewById(R.id.myangebot_text)).setText(angebot.fach);
                            if(editP){
                                View m = v.findViewById(R.id.myangebot_minus);
                                m.setVisibility(View.VISIBLE);
                                m.setOnClickListener(new OnClickListener(){
                                    public void onClick(View view){Dialog.confirm("" + angebot.fach + " wirklich löschen?", new Dialog.ConfirmListener(){
                                        public void ok(){
                                            Internet.angebotEntfernen(angebot.fach, info.id, new Util.Listener(){
                                                public void ok(String data){
                                                    Util.toast("Nachhilfefach wurde gelöscht!");
                                                    update();
                                                }

                                                public void fail(Exception e){}
                                            });
                                        }
                                    });
                                    }
                                });
                            }
                            return v;
                        }
                    };
                    angebote.addView(liste);
                }
            }

            public void fail(){
                angebote.removeAllViews();
                angebote.addView(new MyText("Keine Internetverbindung!"));
            }
        });
    }

    public void onResume(){
        super.onResume();
        load();
    }
}
