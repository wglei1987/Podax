package com.axelby.podax;

import java.io.IOException;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.sax.ElementListener;
import android.sax.RootElement;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PopularPodaxActivity extends ListActivity {

	private class PodaxFeed {
		// public int count;
		public String title;
		public String url;

		@Override
		public String toString() {
			return title;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.innerlist);
		String[] strings = { "Loading from Podax server..." };
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings));

		new AsyncTask<Void, Void, Vector<PodaxFeed>>() {
			@Override
			protected Vector<PodaxFeed> doInBackground(Void... params) {
				final Vector<PodaxFeed> feeds = new Vector<PodaxFeed>();
				RootElement root = new RootElement("feeds");
				root.getChild("feed").setElementListener(new ElementListener() {
					public void start(Attributes attrs) {
						PodaxFeed feed = new PodaxFeed();
						// feed.count = Integer.valueOf(attrs.getValue("count"));
						feed.title = attrs.getValue("title");
						feed.url = attrs.getValue("url");
						feeds.add(feed);
					}
					public void end() {
					}
				});

				HttpGet get = new HttpGet("http://podax.axelby.com/popular.php");
				HttpClient client = new DefaultHttpClient();
				try {
					HttpResponse response = client.execute(get);
					Xml.parse(response.getEntity().getContent(), Xml.Encoding.UTF_8, root.getContentHandler());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
				return feeds;
			}

			@Override
			protected void onPostExecute(Vector<PodaxFeed> result) {
				setListAdapter(new ArrayAdapter<PodaxFeed>(PopularPodaxActivity.this, android.R.layout.simple_list_item_1, result));
				super.onPostExecute(result);
			}
		}.execute((Void)null);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		PodaxFeed feed = (PodaxFeed) l.getItemAtPosition(position);
		Toast.makeText(this, feed.url, Toast.LENGTH_SHORT).show();
		super.onListItemClick(l, v, position, id);
	}

}
