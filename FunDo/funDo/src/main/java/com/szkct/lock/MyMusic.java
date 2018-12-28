package com.szkct.lock;

import java.io.Serializable;

public class MyMusic implements Serializable {

	  String action;
		 String  artistName;
		 String   album ;
		 String  track;
		 boolean playing ;
		 long duration ;
		 long position ;
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		public String getArtistName() {
			return artistName;
		}
		public void setArtistName(String artistName) {
			this.artistName = artistName;
		}
		public String getAlbum() {
			return album;
		}
		public void setAlbum(String album) {
			this.album = album;
		}
		public String getTrack() {
			return track;
		}
		public void setTrack(String track) {
			this.track = track;
		}
		public boolean isPlaying() {
			return playing;
		}
		public void setPlaying(boolean playing) {
			this.playing = playing;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
		public long getPosition() {
			return position;
		}
		public void setPosition(long position) {
			this.position = position;
		}
		 
}
