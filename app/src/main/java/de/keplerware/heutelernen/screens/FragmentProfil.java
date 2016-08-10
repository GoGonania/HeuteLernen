package de.keplerware.heutelernen.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
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

public class FragmentProfil extends MyFragment {
    public UserInfo info;
    private LinearLayout angebote;
    private ImageView bild;

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

        final boolean editP = Sitzung.rang(Rang.MODERATOR) || info.owner();

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
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Profilbild hochladen"), 0);
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
        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                final Uri s = data.getData();
                new Thread(new Runnable(){
                    public void run() {
                        String d;
                        if(s.getScheme().equals(ContentResolver.SCHEME_CONTENT)){
                            final MimeTypeMap mime = MimeTypeMap.getSingleton();
                            d = mime.getExtensionFromMimeType(getContext().getContentResolver().getType(s));
                        }else{
                            d = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(s.getPath())).toString());
                        }

                        try {
                            Util.toastUI("Bild wird hochgeladen...\nBitte warten...");
                            Client c = new Client();
                            c.upload(info.id+"."+d, getContext().getContentResolver().openInputStream(s));
                            c.close();
                            Util.toastUI("Bild wurde hochgeladen!");
                            updatePic(false);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
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
                            if(info.owner()){
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
