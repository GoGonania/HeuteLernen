package de.keplerware.heutelernen;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

import de.keplerware.heutelernen.manager.DataManager;
import de.keplerware.heutelernen.ui.MySpinner;

public class Dialog{
	public interface ConfirmListener{
		void ok();
	}

	public interface PromptListener{
        void ok(String text);
    }

	public interface FachListener{
		void ok(int fach);
	}

	public static void confirm(String text, ConfirmListener li){
		confirm(-1, text, li);
	}

    public static void prompt(String text, String input, final PromptListener l){
        AlertDialog.Builder b = new AlertDialog.Builder(Util.screen);
        final EditText t = new EditText(Util.screen);
        t.setText(input);
        b.setTitle(Util.appname);
        b.setView(t);
        b.setMessage(text);
        b.setIcon(R.drawable.help);
        b.setPositiveButton("OK", new OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                l.ok(t.getText().toString());
            }
        });
        b.setNegativeButton("Abbrechen", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        b.create().show();
    }

	public static void confirm(int icon, String text, final ConfirmListener l){
		AlertDialog.Builder b = new AlertDialog.Builder(Util.screen);
		b.setTitle(Util.appname);
		b.setMessage(text);
		b.setIcon(icon == -1 ? R.drawable.help : icon);
		b.setPositiveButton("Ja", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				l.ok();
			}
		});
		b.setNegativeButton("Nein", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		b.create().show();
	}

	public static void confirmClose(){
		Dialog.confirm("Willst du die App schließen?", new ConfirmListener(){
			public void ok(){
				System.exit(0);
			}
		});
	}

	public static void alert(String titel, String text){
		AlertDialog.Builder b = new AlertDialog.Builder(Util.screen);
		b.setTitle(titel);
		b.setMessage(text);
		b.setPositiveButton("OK", null);
		b.create().show();
	}

	public static void fachSelect(String titel, final FachListener li){
		AlertDialog.Builder b = new AlertDialog.Builder(Util.screen);
		b.setTitle(titel);
		b.setIcon(R.drawable.add);
		View v = Screen.inflate(R.layout.fachselect);
        final MySpinner s = (MySpinner) v.findViewById(R.id.fachselect_spinner);
        s.fill(DataManager.faecher);
        b.setPositiveButton("Auswählen", new OnClickListener(){
			public void onClick(DialogInterface dialogInterface, int i){
				li.ok(s.getSelectedItemPosition());
			}
		});
        b.setView(v);
		b.setNegativeButton("Abbrechen", null);
		b.create().show();
	}

	public static Runnable progress(String text){
		if(text == null || Util.pause()) return null;
		final ProgressDialog d = new ProgressDialog(Util.screen);
		d.setMessage(text);
		d.setTitle(null);
		d.setCancelable(false);
		d.setCanceledOnTouchOutside(false);
		d.show();
		return new Runnable(){
			public void run(){
				d.dismiss();
			}
		};
	}
}
