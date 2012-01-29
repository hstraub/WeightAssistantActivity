package at.linuxhacker.weightassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WeightAssistantActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button buttonNewEntry = ( Button ) findViewById( R.id.buttonAddEntry );
        buttonNewEntry.setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick( View view ) {
	        	startActivity( new Intent( WeightAssistantActivity.this,
	        			AddEntry.class ) );
			}
		} );
        
        Button buttonDisplayData = ( Button ) findViewById( R.id.buttonDisplayData );
        buttonDisplayData.setOnClickListener( new View.OnClickListener( ) {
			@Override
			public void onClick(View v) {
				startActivity( new Intent( WeightAssistantActivity.this,
						DisplayData.class ) );
				
			}
		} );
    }
}