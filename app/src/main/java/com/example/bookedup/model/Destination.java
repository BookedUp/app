package com.example.bookedup.model;

public class Destination {
        private int imageResource;
        private String destinationName;

        public Destination(int imageResource, String destinationName) {
            this.imageResource = imageResource;
            this.destinationName = destinationName;
        }

        public int getImageResource() {
            return imageResource;
        }

        public String getDestinationName() {
            return destinationName;
        }


}
