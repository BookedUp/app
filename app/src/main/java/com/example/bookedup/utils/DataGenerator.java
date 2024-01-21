//package com.example.bookedup.utils;
//
//import com.example.bookedup.model.Accommodation;
//import com.example.bookedup.model.Address;
//import com.example.bookedup.model.DateRange;
//import com.example.bookedup.model.Guest;
//import com.example.bookedup.model.Host;
//import com.example.bookedup.model.Photo;
//import com.example.bookedup.model.PriceChange;
//import com.example.bookedup.model.Reservation;
//import com.example.bookedup.model.User;
//import com.example.bookedup.model.enums.AccommodationStatus;
//import com.example.bookedup.model.enums.AccommodationType;
//import com.example.bookedup.model.enums.Amenity;
//import com.example.bookedup.model.enums.PriceType;
//import com.example.bookedup.model.enums.ReservationStatus;
//import com.example.bookedup.model.enums.Role;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DataGenerator {
//
//    public static List<Address> generateAddresses() {
//        List<Address> addresses = new ArrayList<>();
//
//        addresses.add(new Address("United Kingdom", "London", "W1J 9BR", "150 Piccadilly", true, 51.5074, -0.1278));
//        addresses.add(new Address("France", "Paris", "75008", "10 Place de la Concorde",true, 48.8656, 2.3111));
//        addresses.add(new Address("Germany", "Berlin", "10117", "Unter den Linden 77", true,52.5162, 13.3761));
//        addresses.add(new Address("Russia", "St. Petersburg", "", "Mikhailovskaya Ulitsa 1/7", true,59.9343, 30.3351));
//        addresses.add(new Address("Italy", "Rome", "00187", "Via del Babuino, 9", true,41.9062, 12.4784));
//        addresses.add(new Address("Switzerland", "St. Moritz", "7500", "Via Serlas 27", true,46.4907, 9.8350));
//        addresses.add(new Address("Spain", "Barcelona", "08007", "Passeig de Gràcia, 38-40", true,41.3925, 2.1657));
//        addresses.add(new Address("United Kingdom", "London", "W1K 4HR", "Brook Street, Mayfair", true,51.5094, -0.1493));
//        addresses.add(new Address("Italy", "Venice", "30122", "Riva degli Schiavoni, 4196", true,45.4336, 12.3433));
//        addresses.add(new Address("Italy", "Rome", "00187", "Via Vittorio Veneto, 125", true,41.9064, 12.4881));
//
//        return addresses;
//    }
//
//    public static List<Photo> generatePhotos() {
//        List<Photo> photos = new ArrayList<>();
//
//        photos.add(new Photo("url1", "", true));
//        photos.add(new Photo("url2", "", true));
//        photos.add(new Photo("url3", "", true));
//        photos.add(new Photo("url4", "", true));
//        photos.add(new Photo("url5", "", true));
//        photos.add(new Photo("url6", "", true));
//        photos.add(new Photo("url7", "", true));
//        photos.add(new Photo("url8", "", true));
//        photos.add(new Photo("url9", "", true));
//        photos.add(new Photo("url10", "", true));
//
//        return photos;
//    }
//
//    public static List<User> generateUsers() {
//        List<User> users = new ArrayList<>();
//        List<Address> addresses = generateAddresses();
//        List<Photo> photos = generatePhotos();
//
//        users.add(new User("Jovan", "Jovanović", addresses.get(0), 123, "jovan.jovanovic@example.com", "jovanpass", false, true, true, photos.get(0), Role.ADMIN));
//        users.add(new User("Ana", "Anić", addresses.get(1), 987654321, "ana.anic@example.com", "anapass", false, true,true,photos.get(1),Role.HOST));
//        users.add(new User("Milica", "Milosavljević", addresses.get(2), 987654322, "milica.milosavljevic@example.com", "milicinapass", false, true,true,photos.get(2),Role.HOST));
//        users.add(new User("Marko", "Marković", addresses.get(3), 0631234567, "marko.markovic@example.com", "markopass",  false, true,true,photos.get(3),Role.GUEST));
//        users.add(new User("Jovana", "Jovanović", addresses.get(4), 0631234567, "jovana.jovanovic@example.com", "jovanapass", false, true,true,photos.get(4),Role.GUEST));
//        users.add(new User("Nenad", "Nenadić", addresses.get(5), 0631234567, "nenad.nenadic@example.com", "nenadpass", false, true,true,photos.get(5),Role.GUEST));
//        users.add(new User("Mila", "Milićević", addresses.get(6), 0661122334, "mila.milicevic@example.com", "milinpass", false, true,true,photos.get(6),Role.GUEST));
//
//
//        return users;
//    }
//
//    public static List<List<PriceChange>> generatePriceChanges() {
//        List<List<PriceChange>> allPriceChanges = new ArrayList<>();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
//
//        try {
//
//            List<PriceChange> dateRanges1 = new ArrayList<>();
//
//            dateRanges1.add(new PriceChange(sdf.parse("2023-12-05 13:00:00.000000"), 120.0));
//            dateRanges1.add(new PriceChange(sdf.parse("2023-12-15 13:00:00.000000"), 130.0));
//
//            allPriceChanges.add(dateRanges1);
//
//
//            List<PriceChange> dateRanges2 = new ArrayList<>();
//
//            dateRanges2.add(new PriceChange(sdf.parse("2023-12-08 13:00:00.000000"), 150.0));
//            dateRanges2.add(new PriceChange(sdf.parse("2023-12-20 13:00:00.000000"), 160.0));
//
//            allPriceChanges.add(dateRanges2);
//
//            List<PriceChange> dateRanges3 = new ArrayList<>();
//
//            dateRanges3.add(new PriceChange(sdf.parse("2023-12-14 13:00:00.00000"), 100.0));
//            dateRanges3.add(new PriceChange(sdf.parse("2023-12-28 13:00:00.000000"), 110.0));
//
//            allPriceChanges.add(dateRanges3);
//
//            List<PriceChange> dateRanges4 = new ArrayList<>();
//
//            dateRanges4.add(new PriceChange(sdf.parse("2023-12-08 13:00:00.000000"), 180.0));
//
//            allPriceChanges.add(dateRanges4);
//
//            List<PriceChange> dateRanges5 = new ArrayList<>();
//
//            dateRanges5.add(new PriceChange(sdf.parse("2023-12-20 13:00:00.000000"), 200.0));
//            dateRanges5.add(new PriceChange(sdf.parse("2023-12-26 13:00:00.000000"), 220.0));
//
//            allPriceChanges.add(dateRanges5);
//
//            List<PriceChange> dateRanges6 = new ArrayList<>();
//
//            dateRanges6.add(new PriceChange(sdf.parse("2023-12-12 13:00:00.000000"), 130.0));
//
//            allPriceChanges.add(dateRanges6);
//
//            List<PriceChange> dateRanges7 = new ArrayList<>();
//
//            dateRanges7.add(new PriceChange(sdf.parse("22023-12-15 13:00:00.000000"),90.0));
//
//            allPriceChanges.add(dateRanges7);
//
//            List<PriceChange> dateRanges8 = new ArrayList<>();
//
//            dateRanges8.add(new PriceChange(sdf.parse("2023-12-25 13:00:00.000000"), 170.0));
//            dateRanges8.add(new PriceChange(sdf.parse("2024-01-05 13:00:00.000000"), 190.0));
//
//            allPriceChanges.add(dateRanges8);
//
//            List<PriceChange> dateRanges9 = new ArrayList<>();
//
//            dateRanges9.add(new PriceChange(sdf.parse("2023-12-20 13:00:00.000000"), 120.0));
//            dateRanges9.add(new PriceChange(sdf.parse("2023-12-30 13:00:00.000000"), 130.0));
//
//            allPriceChanges.add(dateRanges9);
//
//            List<PriceChange> dateRanges10 = new ArrayList<>();
//
//            dateRanges10.add(new PriceChange(sdf.parse("2023-12-18 13:00:00.000000"),110.0));
//            dateRanges10.add(new PriceChange(sdf.parse("2023-12-28 13:00:00.000000"), 120.0));
//
//            allPriceChanges.add(dateRanges10);
//
//
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return allPriceChanges;
//    }
//
//    public static List<List<DateRange>> generateDateRanges() {
//        List<List<DateRange>> allDateRanges = new ArrayList<>();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
//
//        try {
//
//            List<DateRange> dateRanges1 = new ArrayList<>();
//
//            dateRanges1.add(new DateRange(sdf.parse("2023-12-01 13:00:00.000000"), sdf.parse("2023-12-10 13:00:00.000000")));
//            dateRanges1.add(new DateRange(sdf.parse("2023-12-15 13:00:00.000000"), sdf.parse("2023-12-20 13:00:00.000000")));
//
//            allDateRanges.add(dateRanges1);
//
//
//            List<DateRange> dateRanges2 = new ArrayList<>();
//
//            dateRanges2.add(new DateRange(sdf.parse("2023-12-05 13:00:00.000000"), sdf.parse("2023-12-12 13:00:00.000000")));
//            dateRanges2.add(new DateRange(sdf.parse("2023-12-18 13:00:00.000000"), sdf.parse("2023-12-25 13:00:00.000000")));
//
//            allDateRanges.add(dateRanges2);
//
//            List<DateRange> dateRanges3 = new ArrayList<>();
//
//            dateRanges3.add(new DateRange(sdf.parse("2023-12-08 13:00:00.000000"), sdf.parse("2023-12-14 13:00:00.000000")));
//            dateRanges3.add(new DateRange(sdf.parse("2023-12-21 13:00:00.000000"), sdf.parse("2023-12-28 13:00:00.000000")));
//
//            allDateRanges.add(dateRanges3);
//
//            List<DateRange> dateRanges4 = new ArrayList<>();
//
//            dateRanges4.add(new DateRange(sdf.parse("2023-12-20 13:00:00.000000"), sdf.parse("2024-01-09 13:00:00.000000")));
//
//            allDateRanges.add(dateRanges4);
//
//            List<DateRange> dateRanges5 = new ArrayList<>();
//
//            dateRanges5.add(new DateRange(sdf.parse("2023-12-18 13:00:00.000000"), sdf.parse("2023-12-24 13:00:00.000000")));
//            dateRanges5.add(new DateRange(sdf.parse("2023-12-25 13:00:00.000000"), sdf.parse("2024-01-28 13:00:00.000000")));
//
//            allDateRanges.add(dateRanges5);
//
//            List<DateRange> dateRanges6 = new ArrayList<>();
//
//            dateRanges6.add(new DateRange(sdf.parse("2023-12-03 13:00:00.000000"), sdf.parse("2023-12-11 13:00:00.000000")));
//
//            allDateRanges.add(dateRanges6);
//
//            List<DateRange> dateRanges7 = new ArrayList<>();
//
//            dateRanges7.add(new DateRange(sdf.parse("2023-12-07 13:00:00.000000"), sdf.parse("2023-12-14 13:00:00.000000")));
//
//            allDateRanges.add(dateRanges7);
//
//            List<DateRange> dateRanges8 = new ArrayList<>();
//
//            dateRanges8.add(new DateRange(sdf.parse("2023-12-14 13:00:00.000000"), sdf.parse("2023-12-21 13:00:00.000000")));
//            dateRanges8.add(new DateRange(sdf.parse("2023-12-28 13:00:00.000000"), sdf.parse("2024-01-04 13:00:00.000000")));
//
//            allDateRanges.add(dateRanges8);
//
//            List<DateRange> dateRanges9 = new ArrayList<>();
//
//            dateRanges9.add(new DateRange(sdf.parse("2023-12-10 13:00:00.000000"), sdf.parse("2023-12-17 13:00:00.000000")));
//            dateRanges9.add(new DateRange(sdf.parse("2023-12-22 13:00:00.000000"), sdf.parse("2023-12-29 13:00:00.000000")));
//
//            allDateRanges.add(dateRanges9);
//
//            List<DateRange> dateRanges10 = new ArrayList<>();
//
//            dateRanges10.add(new DateRange(sdf.parse("2023-12-05 13:00:00.000000"), sdf.parse("2023-12-12 13:00:00.000000")));
//            dateRanges10.add(new DateRange(sdf.parse("2023-12-19 13:00:00.000000"), sdf.parse("2023-12-26 13:00:00.000000")));
//
//            allDateRanges.add(dateRanges10);
//
//
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return allDateRanges;
//    }
//
//    public static List<Accommodation> generateAccommodations() {
//        List<Accommodation> accommodations = new ArrayList<>();
//        List<Address> addresses = generateAddresses();
//        List<List<DateRange>> allDateRanges = generateDateRanges();
//        List<List<PriceChange>> allPriceChanges = generatePriceChanges();
//
//
//        List<Amenity> amenities1 = new ArrayList<>();
//        amenities1.add(Amenity.FREE_WIFI);
//        amenities1.add(Amenity.NON_SMOKING_ROOMS);
//        amenities1.add(Amenity.PARKING);
//
//        List<Amenity> amenities2 = new ArrayList<>();
//        amenities2.add(Amenity.RESTAURANT);
//        amenities2.add(Amenity.SWIMMING_POOL);
//
//        List<Amenity> amenities3 = new ArrayList<>();
//        amenities3.add(Amenity.FREE_WIFI);
//        amenities3.add(Amenity.FITNESS_CENTRE);
//
//        List<Amenity> amenities4 = new ArrayList<>();
//        amenities4.add(Amenity.NON_SMOKING_ROOMS);
//        amenities4.add(Amenity.PARKING);
//        amenities4.add(Amenity.RESTAURANT);
//
//        List<Amenity> amenities5 = new ArrayList<>();
//        amenities5.add(Amenity.FREE_WIFI);
//        amenities5.add(Amenity.NON_SMOKING_ROOMS);
//
//        List<Amenity> amenities6 = new ArrayList<>();
//        amenities6.add(Amenity.PARKING);
//
//        List<Amenity> amenities7 = new ArrayList<>();
//        amenities7.add(Amenity.RESTAURANT);
//        amenities7.add(Amenity.SWIMMING_POOL);
//
//        List<Amenity> amenities8 = new ArrayList<>();
//        amenities8.add(Amenity.FREE_WIFI);
//        amenities8.add(Amenity.FITNESS_CENTRE);
//
//        List<Amenity> amenities9 = new ArrayList<>();
//        amenities9.add(Amenity.NON_SMOKING_ROOMS);
//        amenities9.add(Amenity.PARKING);
//        amenities9.add(Amenity.RESTAURANT);
//
//        List<Amenity> amenities10 = new ArrayList<>();
//        amenities10.add(Amenity.FREE_WIFI);
//        amenities10.add(Amenity.FITNESS_CENTRE);
//
//        List<Photo> photos = new ArrayList<>();
//
//        Host host = new Host();
//
//        accommodations.add(new Accommodation("OceanViewEscape", "Indulge in panoramic ocean views from this luxurious coastal retreat. The three-bedroom villa features contemporary design, a fully equipped gourmet kitchen, and a private terrace for soaking in the sea breeze. Enjoy access to resort-style amenities, including a beachfront pool and spa. Ideal for a tranquil getaway or entertaining guests.", addresses.get(0),amenities1,photos,2,4, AccommodationType.HOTEL,allDateRanges.get(0), PriceType.PER_NIGHT, allPriceChanges.get(0),true, AccommodationStatus.CREATED,host, 200.0, 0.0, 7.8, 7));
//        accommodations.add(new Accommodation("Sky High Suite", "Elevate your stay in the luxurious Sky High Suite, situated on the top floors of a landmark skyscraper. This opulent three-bedroom suite offers panoramic city views through floor-to-ceiling windows, a private chef's kitchen, and a lavish entertainment lounge. Indulge in the epitome of sophistication and convenience in the heart of the urban skyline.", addresses.get(1),amenities2,photos,1,3,AccommodationType.HOSTEL,allDateRanges.get(1),PriceType.PER_GUEST,allPriceChanges.get(1),true,AccommodationStatus.ACTIVE,host, 50.0, 0.0, 8.0, 3));
//        accommodations.add(new Accommodation("City Lights Penthouse", "Experience the epitome of urban living in this breathtaking penthouse with floor-to-ceiling windows showcasing city lights. Boasting four bedrooms and a stylish modern interior, this high-rise oasis offers a fully stocked bar, entertainment lounge, and a private rooftop terrace with skyline views. Elevate your stay in the heart of the city.",addresses.get(2),amenities3,photos,4,8,AccommodationType.VILLA,allDateRanges.get(2),PriceType.PER_NIGHT, allPriceChanges.get(2),false,AccommodationStatus.CHANGED,host, 300.0,0.0,  9.0, 14));
//        accommodations.add(new Accommodation("Alpine Retreat Chalet", "Nestled in the serene alpine landscape, this chalet offers a cozy escape with log cabin charm. Featuring two bedrooms, a wood-burning fireplace, and a hot tub on the deck, it's an idyllic setting for a mountain retreat. Enjoy direct access to hiking trails and ski slopes for year-round outdoor adventures.", addresses.get(3),amenities4,photos,1,2,AccommodationType.APARTMENT,allDateRanges.get(3),PriceType.PER_NIGHT,allPriceChanges.get(3),false, AccommodationStatus.ACTIVE,host,80.0, 0.0, 9.5, 2));
//        accommodations.add(new Accommodation("Urban Retreat Suite", "Welcome to your new urban oasis! This stylish two-bedroom apartment boasts modern elegance with an open-concept design, featuring sleek hardwood floors and large windows that flood the space with natural light. The kitchen is a culinary enthusiast's dream, equipped with state-of-the-art stainless steel appliances and granite countertops. Unwind in the spacious living room, perfect for entertaining or cozy nights in, and retreat to the master bedroom with its en-suite bathroom for the ultimate in comfort. Located in a vibrant neighborhood, this apartment offers not just a home but a lifestyle, with trendy shops, restaurants, and parks just steps away.", addresses.get(4), amenities5, photos,2, 6, AccommodationType.RESORT,allDateRanges.get(4),PriceType.PER_NIGHT,allPriceChanges.get(4),true, AccommodationStatus.ACTIVE,host,500.0, 0.0,7.5,14));
//        accommodations.add(new Accommodation("Historic Inn Elegance", "Step back in time at this meticulously restored historic inn. Each room tells a story with antique furnishings and period decor. Indulge in a continental breakfast in the charming courtyard and explore nearby landmarks. Immerse yourself in the elegance of a bygone era while enjoying modern comforts.", addresses.get(5),amenities6, photos,2,4,AccommodationType.APARTMENT,allDateRanges.get(5),PriceType.PER_NIGHT,allPriceChanges.get(5),false,AccommodationStatus.CHANGED,host,120.0,0.0,8.8,7));
//        accommodations.add(new Accommodation("Skyline View Loft", "Perched atop a skyscraper, this contemporary loft offers unparalleled skyline views. The one-bedroom space features minimalist aesthetics, a fully equipped kitchen, and a spacious living area. Enjoy the luxury of a private balcony overlooking the city lights for a truly cosmopolitan experience.", addresses.get(6),amenities7,photos,6,10,AccommodationType.VILLA,allDateRanges.get(6),PriceType.PER_NIGHT,allPriceChanges.get(6),false,AccommodationStatus.ACTIVE,host, 450.0, 0.0,10.0, 14));
//        accommodations.add(new Accommodation("Tranquil Cottage Retreat", "Escape to the countryside in this charming cottage surrounded by lush gardens. The two-bedroom retreat features a rustic fireplace, a fully equipped kitchen, and a private patio for enjoying morning coffee or evening stargazing. Immerse yourself in the tranquility of nature while being just a short drive from local vineyards and hiking trails.", addresses.get(7), amenities8,photos,2,4,AccommodationType.HOTEL,allDateRanges.get(7),PriceType.PER_NIGHT,allPriceChanges.get(7),true,AccommodationStatus.ACTIVE,host,300.0, 0.0, 9.8, 7));
//        accommodations.add(new Accommodation("Beachfront Bungalow Paradise", "Experience paradise at this beachfront bungalow with direct access to the sandy shores. The tropical-themed two-bedroom haven offers a sun-soaked deck, hammocks for lazy afternoons, and stunning sunset views. Embrace the laid-back coastal lifestyle with surf lessons, beach picnics, and seaside relaxation.",addresses.get(8),amenities9,photos,1,3,AccommodationType.VILLA,allDateRanges.get(8),PriceType.PER_NIGHT,allPriceChanges.get(8),true, AccommodationStatus.ACTIVE,host,70.0,0.0, 8.4,3));
//        accommodations.add(new Accommodation("Riverside Retreat Cabin", "Unplug and unwind in this secluded cabin nestled by a serene river. The one-bedroom hideaway features a woodsy interior, a riverside deck for fishing or lounging, and a cozy fireplace. Disconnect from the hustle and bustle, and reconnect with nature in this peaceful riverside retreat.", addresses.get(9), amenities10,photos,2,4,AccommodationType.APARTMENT,allDateRanges.get(9),PriceType.PER_NIGHT,allPriceChanges.get(9),true,AccommodationStatus.ACTIVE,host, 90.0, 0.0, 8.7, 5));
//
//        return accommodations;
//    }
//
//
//    public static List<Accommodation> generateChangedAccommodationsRequests() {
//        List<Accommodation> accommodations = new ArrayList<>();
//        List<Address> addresses = generateAddresses();
//        List<List<DateRange>> allDateRanges = generateDateRanges();
//        List<List<PriceChange>> allPriceChanges = generatePriceChanges();
//
//
//        List<Amenity> amenities1 = new ArrayList<>();
//        amenities1.add(Amenity.FREE_WIFI);
//        amenities1.add(Amenity.NON_SMOKING_ROOMS);
//        amenities1.add(Amenity.PARKING);
//
//        List<Amenity> amenities2 = new ArrayList<>();
//        amenities2.add(Amenity.RESTAURANT);
//        amenities2.add(Amenity.SWIMMING_POOL);
//
//        List<Amenity> amenities3 = new ArrayList<>();
//        amenities3.add(Amenity.FREE_WIFI);
//        amenities3.add(Amenity.FITNESS_CENTRE);
//
//        List<Amenity> amenities4 = new ArrayList<>();
//        amenities4.add(Amenity.NON_SMOKING_ROOMS);
//        amenities4.add(Amenity.PARKING);
//        amenities4.add(Amenity.RESTAURANT);
//
//        List<Amenity> amenities5 = new ArrayList<>();
//        amenities5.add(Amenity.FREE_WIFI);
//        amenities5.add(Amenity.NON_SMOKING_ROOMS);
//
//        List<Amenity> amenities6 = new ArrayList<>();
//        amenities6.add(Amenity.PARKING);
//
//        List<Amenity> amenities7 = new ArrayList<>();
//        amenities7.add(Amenity.RESTAURANT);
//        amenities7.add(Amenity.SWIMMING_POOL);
//
//        List<Amenity> amenities8 = new ArrayList<>();
//        amenities8.add(Amenity.FREE_WIFI);
//        amenities8.add(Amenity.FITNESS_CENTRE);
//
//        List<Amenity> amenities9 = new ArrayList<>();
//        amenities9.add(Amenity.NON_SMOKING_ROOMS);
//        amenities9.add(Amenity.PARKING);
//        amenities9.add(Amenity.RESTAURANT);
//
//        List<Amenity> amenities10 = new ArrayList<>();
//        amenities10.add(Amenity.FREE_WIFI);
//        amenities10.add(Amenity.FITNESS_CENTRE);
//
//        List<Photo> photos = new ArrayList<>();
//
//        Host host = new Host();
//
////        accommodations.add(new Accommodation("OceanViewEscape", "Indulge in panoramic ocean views from this luxurious coastal retreat. The three-bedroom villa features contemporary design, a fully equipped gourmet kitchen, and a private terrace for soaking in the sea breeze. Enjoy access to resort-style amenities, including a beachfront pool and spa. Ideal for a tranquil getaway or entertaining guests.", addresses.get(0),amenities1,photos,2,4, AccommodationType.HOTEL,allDateRanges.get(0), PriceType.PER_NIGHT, allPriceChanges.get(0),true, AccommodationStatus.CREATED,host, 200.0, 0.0, 7.8, 7));
////        accommodations.add(new Accommodation("Sky High Suite", "Elevate your stay in the luxurious Sky High Suite, situated on the top floors of a landmark skyscraper. This opulent three-bedroom suite offers panoramic city views through floor-to-ceiling windows, a private chef's kitchen, and a lavish entertainment lounge. Indulge in the epitome of sophistication and convenience in the heart of the urban skyline.", addresses.get(1),amenities2,photos,1,3,AccommodationType.HOSTEL,allDateRanges.get(1),PriceType.PER_GUEST,allPriceChanges.get(1),true,AccommodationStatus.ACTIVE,host, 50.0, 0.0, 8.0, 3));
//        accommodations.add(new Accommodation("City Lights Penthouse", "Experience the epitome of urban living in this breathtaking penthouse with floor-to-ceiling windows showcasing city lights. Boasting four bedrooms and a stylish modern interior, this high-rise oasis offers a fully stocked bar, entertainment lounge, and a private rooftop terrace with skyline views. Elevate your stay in the heart of the city.",addresses.get(2),amenities3,photos,4,8,AccommodationType.VILLA,allDateRanges.get(2),PriceType.PER_NIGHT, allPriceChanges.get(2),false,AccommodationStatus.CHANGED,host, 300.0,0.0,  9.0, 14));
////        accommodations.add(new Accommodation("Alpine Retreat Chalet", "Nestled in the serene alpine landscape, this chalet offers a cozy escape with log cabin charm. Featuring two bedrooms, a wood-burning fireplace, and a hot tub on the deck, it's an idyllic setting for a mountain retreat. Enjoy direct access to hiking trails and ski slopes for year-round outdoor adventures.", addresses.get(3),amenities4,photos,1,2,AccommodationType.APARTMENT,allDateRanges.get(3),PriceType.PER_NIGHT,allPriceChanges.get(3),false, AccommodationStatus.ACTIVE,host,80.0, 0.0, 9.5, 2));
////        accommodations.add(new Accommodation("Urban Retreat Suite", "Welcome to your new urban oasis! This stylish two-bedroom apartment boasts modern elegance with an open-concept design, featuring sleek hardwood floors and large windows that flood the space with natural light. The kitchen is a culinary enthusiast's dream, equipped with state-of-the-art stainless steel appliances and granite countertops. Unwind in the spacious living room, perfect for entertaining or cozy nights in, and retreat to the master bedroom with its en-suite bathroom for the ultimate in comfort. Located in a vibrant neighborhood, this apartment offers not just a home but a lifestyle, with trendy shops, restaurants, and parks just steps away.", addresses.get(4), amenities5, photos,2, 6, AccommodationType.RESORT,allDateRanges.get(4),PriceType.PER_NIGHT,allPriceChanges.get(4),true, AccommodationStatus.ACTIVE,host,500.0, 0.0,7.5,14));
//        accommodations.add(new Accommodation("Historic Inn Elegance", "Step back in time at this meticulously restored historic inn. Each room tells a story with antique furnishings and period decor. Indulge in a continental breakfast in the charming courtyard and explore nearby landmarks. Immerse yourself in the elegance of a bygone era while enjoying modern comforts.", addresses.get(5),amenities6, photos,2,4,AccommodationType.APARTMENT,allDateRanges.get(5),PriceType.PER_NIGHT,allPriceChanges.get(5),false,AccommodationStatus.CHANGED,host,120.0,0.0,8.8,7));
////        accommodations.add(new Accommodation("Skyline View Loft", "Perched atop a skyscraper, this contemporary loft offers unparalleled skyline views. The one-bedroom space features minimalist aesthetics, a fully equipped kitchen, and a spacious living area. Enjoy the luxury of a private balcony overlooking the city lights for a truly cosmopolitan experience.", addresses.get(6),amenities7,photos,6,10,AccommodationType.VILLA,allDateRanges.get(6),PriceType.PER_NIGHT,allPriceChanges.get(6),false,AccommodationStatus.ACTIVE,host, 450.0, 0.0,10.0, 14));
////        accommodations.add(new Accommodation("Tranquil Cottage Retreat", "Escape to the countryside in this charming cottage surrounded by lush gardens. The two-bedroom retreat features a rustic fireplace, a fully equipped kitchen, and a private patio for enjoying morning coffee or evening stargazing. Immerse yourself in the tranquility of nature while being just a short drive from local vineyards and hiking trails.", addresses.get(7), amenities8,photos,2,4,AccommodationType.HOTEL,allDateRanges.get(7),PriceType.PER_NIGHT,allPriceChanges.get(7),true,AccommodationStatus.ACTIVE,host,300.0, 0.0, 9.8, 7));
////        accommodations.add(new Accommodation("Beachfront Bungalow Paradise", "Experience paradise at this beachfront bungalow with direct access to the sandy shores. The tropical-themed two-bedroom haven offers a sun-soaked deck, hammocks for lazy afternoons, and stunning sunset views. Embrace the laid-back coastal lifestyle with surf lessons, beach picnics, and seaside relaxation.",addresses.get(8),amenities9,photos,1,3,AccommodationType.VILLA,allDateRanges.get(8),PriceType.PER_NIGHT,allPriceChanges.get(8),true, AccommodationStatus.ACTIVE,host,70.0,0.0, 8.4,3));
////        accommodations.add(new Accommodation("Riverside Retreat Cabin", "Unplug and unwind in this secluded cabin nestled by a serene river. The one-bedroom hideaway features a woodsy interior, a riverside deck for fishing or lounging, and a cozy fireplace. Disconnect from the hustle and bustle, and reconnect with nature in this peaceful riverside retreat.", addresses.get(9), amenities10,photos,2,4,AccommodationType.APARTMENT,allDateRanges.get(9),PriceType.PER_NIGHT,allPriceChanges.get(9),true,AccommodationStatus.ACTIVE,host, 90.0, 0.0, 8.7, 5));
//
//        return accommodations;
//    }
//
//
//    public static List<Accommodation> generateAllAccommodationsRequests() {
//        List<Accommodation> accommodations = new ArrayList<>();
//        List<Address> addresses = generateAddresses();
//        List<List<DateRange>> allDateRanges = generateDateRanges();
//        List<List<PriceChange>> allPriceChanges = generatePriceChanges();
//
//
//        List<Amenity> amenities1 = new ArrayList<>();
//        amenities1.add(Amenity.FREE_WIFI);
//        amenities1.add(Amenity.NON_SMOKING_ROOMS);
//        amenities1.add(Amenity.PARKING);
//
//        List<Amenity> amenities2 = new ArrayList<>();
//        amenities2.add(Amenity.RESTAURANT);
//        amenities2.add(Amenity.SWIMMING_POOL);
//
//        List<Amenity> amenities3 = new ArrayList<>();
//        amenities3.add(Amenity.FREE_WIFI);
//        amenities3.add(Amenity.FITNESS_CENTRE);
//
//        List<Amenity> amenities4 = new ArrayList<>();
//        amenities4.add(Amenity.NON_SMOKING_ROOMS);
//        amenities4.add(Amenity.PARKING);
//        amenities4.add(Amenity.RESTAURANT);
//
//        List<Amenity> amenities5 = new ArrayList<>();
//        amenities5.add(Amenity.FREE_WIFI);
//        amenities5.add(Amenity.NON_SMOKING_ROOMS);
//
//        List<Amenity> amenities6 = new ArrayList<>();
//        amenities6.add(Amenity.PARKING);
//
//        List<Amenity> amenities7 = new ArrayList<>();
//        amenities7.add(Amenity.RESTAURANT);
//        amenities7.add(Amenity.SWIMMING_POOL);
//
//        List<Amenity> amenities8 = new ArrayList<>();
//        amenities8.add(Amenity.FREE_WIFI);
//        amenities8.add(Amenity.FITNESS_CENTRE);
//
//        List<Amenity> amenities9 = new ArrayList<>();
//        amenities9.add(Amenity.NON_SMOKING_ROOMS);
//        amenities9.add(Amenity.PARKING);
//        amenities9.add(Amenity.RESTAURANT);
//
//        List<Amenity> amenities10 = new ArrayList<>();
//        amenities10.add(Amenity.FREE_WIFI);
//        amenities10.add(Amenity.FITNESS_CENTRE);
//
//        List<Photo> photos = new ArrayList<>();
//
//        Host host = new Host();
//
//        accommodations.add(new Accommodation("OceanViewEscape", "Indulge in panoramic ocean views from this luxurious coastal retreat. The three-bedroom villa features contemporary design, a fully equipped gourmet kitchen, and a private terrace for soaking in the sea breeze. Enjoy access to resort-style amenities, including a beachfront pool and spa. Ideal for a tranquil getaway or entertaining guests.", addresses.get(0),amenities1,photos,2,4, AccommodationType.HOTEL,allDateRanges.get(0), PriceType.PER_NIGHT, allPriceChanges.get(0),true, AccommodationStatus.CREATED,host, 200.0, 0.0, 7.8, 7));
////        accommodations.add(new Accommodation("Sky High Suite", "Elevate your stay in the luxurious Sky High Suite, situated on the top floors of a landmark skyscraper. This opulent three-bedroom suite offers panoramic city views through floor-to-ceiling windows, a private chef's kitchen, and a lavish entertainment lounge. Indulge in the epitome of sophistication and convenience in the heart of the urban skyline.", addresses.get(1),amenities2,photos,1,3,AccommodationType.HOSTEL,allDateRanges.get(1),PriceType.PER_GUEST,allPriceChanges.get(1),true,AccommodationStatus.ACTIVE,host, 50.0, 0.0, 8.0, 3));
//        accommodations.add(new Accommodation("City Lights Penthouse", "Experience the epitome of urban living in this breathtaking penthouse with floor-to-ceiling windows showcasing city lights. Boasting four bedrooms and a stylish modern interior, this high-rise oasis offers a fully stocked bar, entertainment lounge, and a private rooftop terrace with skyline views. Elevate your stay in the heart of the city.",addresses.get(2),amenities3,photos,4,8,AccommodationType.VILLA,allDateRanges.get(2),PriceType.PER_NIGHT, allPriceChanges.get(2),false,AccommodationStatus.CHANGED,host, 300.0,0.0,  9.0, 14));
////        accommodations.add(new Accommodation("Alpine Retreat Chalet", "Nestled in the serene alpine landscape, this chalet offers a cozy escape with log cabin charm. Featuring two bedrooms, a wood-burning fireplace, and a hot tub on the deck, it's an idyllic setting for a mountain retreat. Enjoy direct access to hiking trails and ski slopes for year-round outdoor adventures.", addresses.get(3),amenities4,photos,1,2,AccommodationType.APARTMENT,allDateRanges.get(3),PriceType.PER_NIGHT,allPriceChanges.get(3),false, AccommodationStatus.ACTIVE,host,80.0, 0.0, 9.5, 2));
////        accommodations.add(new Accommodation("Urban Retreat Suite", "Welcome to your new urban oasis! This stylish two-bedroom apartment boasts modern elegance with an open-concept design, featuring sleek hardwood floors and large windows that flood the space with natural light. The kitchen is a culinary enthusiast's dream, equipped with state-of-the-art stainless steel appliances and granite countertops. Unwind in the spacious living room, perfect for entertaining or cozy nights in, and retreat to the master bedroom with its en-suite bathroom for the ultimate in comfort. Located in a vibrant neighborhood, this apartment offers not just a home but a lifestyle, with trendy shops, restaurants, and parks just steps away.", addresses.get(4), amenities5, photos,2, 6, AccommodationType.RESORT,allDateRanges.get(4),PriceType.PER_NIGHT,allPriceChanges.get(4),true, AccommodationStatus.ACTIVE,host,500.0, 0.0,7.5,14));
//        accommodations.add(new Accommodation("Historic Inn Elegance", "Step back in time at this meticulously restored historic inn. Each room tells a story with antique furnishings and period decor. Indulge in a continental breakfast in the charming courtyard and explore nearby landmarks. Immerse yourself in the elegance of a bygone era while enjoying modern comforts.", addresses.get(5),amenities6, photos,2,4,AccommodationType.APARTMENT,allDateRanges.get(5),PriceType.PER_NIGHT,allPriceChanges.get(5),false,AccommodationStatus.CHANGED,host,120.0,0.0,8.8,7));
////        accommodations.add(new Accommodation("Skyline View Loft", "Perched atop a skyscraper, this contemporary loft offers unparalleled skyline views. The one-bedroom space features minimalist aesthetics, a fully equipped kitchen, and a spacious living area. Enjoy the luxury of a private balcony overlooking the city lights for a truly cosmopolitan experience.", addresses.get(6),amenities7,photos,6,10,AccommodationType.VILLA,allDateRanges.get(6),PriceType.PER_NIGHT,allPriceChanges.get(6),false,AccommodationStatus.ACTIVE,host, 450.0, 0.0,10.0, 14));
////        accommodations.add(new Accommodation("Tranquil Cottage Retreat", "Escape to the countryside in this charming cottage surrounded by lush gardens. The two-bedroom retreat features a rustic fireplace, a fully equipped kitchen, and a private patio for enjoying morning coffee or evening stargazing. Immerse yourself in the tranquility of nature while being just a short drive from local vineyards and hiking trails.", addresses.get(7), amenities8,photos,2,4,AccommodationType.HOTEL,allDateRanges.get(7),PriceType.PER_NIGHT,allPriceChanges.get(7),true,AccommodationStatus.ACTIVE,host,300.0, 0.0, 9.8, 7));
////        accommodations.add(new Accommodation("Beachfront Bungalow Paradise", "Experience paradise at this beachfront bungalow with direct access to the sandy shores. The tropical-themed two-bedroom haven offers a sun-soaked deck, hammocks for lazy afternoons, and stunning sunset views. Embrace the laid-back coastal lifestyle with surf lessons, beach picnics, and seaside relaxation.",addresses.get(8),amenities9,photos,1,3,AccommodationType.VILLA,allDateRanges.get(8),PriceType.PER_NIGHT,allPriceChanges.get(8),true, AccommodationStatus.ACTIVE,host,70.0,0.0, 8.4,3));
////        accommodations.add(new Accommodation("Riverside Retreat Cabin", "Unplug and unwind in this secluded cabin nestled by a serene river. The one-bedroom hideaway features a woodsy interior, a riverside deck for fishing or lounging, and a cozy fireplace. Disconnect from the hustle and bustle, and reconnect with nature in this peaceful riverside retreat.", addresses.get(9), amenities10,photos,2,4,AccommodationType.APARTMENT,allDateRanges.get(9),PriceType.PER_NIGHT,allPriceChanges.get(9),true,AccommodationStatus.ACTIVE,host, 90.0, 0.0, 8.7, 5));
//
//        return accommodations;
//    }
//
//
//    public static List<Accommodation> generateNewAccommodationsRequests() {
//        List<Accommodation> accommodations = new ArrayList<>();
//        List<Address> addresses = generateAddresses();
//        List<List<DateRange>> allDateRanges = generateDateRanges();
//        List<List<PriceChange>> allPriceChanges = generatePriceChanges();
//
//
//        List<Amenity> amenities1 = new ArrayList<>();
//        amenities1.add(Amenity.FREE_WIFI);
//        amenities1.add(Amenity.NON_SMOKING_ROOMS);
//        amenities1.add(Amenity.PARKING);
//
//        List<Amenity> amenities2 = new ArrayList<>();
//        amenities2.add(Amenity.RESTAURANT);
//        amenities2.add(Amenity.SWIMMING_POOL);
//
//        List<Amenity> amenities3 = new ArrayList<>();
//        amenities3.add(Amenity.FREE_WIFI);
//        amenities3.add(Amenity.FITNESS_CENTRE);
//
//        List<Amenity> amenities4 = new ArrayList<>();
//        amenities4.add(Amenity.NON_SMOKING_ROOMS);
//        amenities4.add(Amenity.PARKING);
//        amenities4.add(Amenity.RESTAURANT);
//
//        List<Amenity> amenities5 = new ArrayList<>();
//        amenities5.add(Amenity.FREE_WIFI);
//        amenities5.add(Amenity.NON_SMOKING_ROOMS);
//
//        List<Amenity> amenities6 = new ArrayList<>();
//        amenities6.add(Amenity.PARKING);
//
//        List<Amenity> amenities7 = new ArrayList<>();
//        amenities7.add(Amenity.RESTAURANT);
//        amenities7.add(Amenity.SWIMMING_POOL);
//
//        List<Amenity> amenities8 = new ArrayList<>();
//        amenities8.add(Amenity.FREE_WIFI);
//        amenities8.add(Amenity.FITNESS_CENTRE);
//
//        List<Amenity> amenities9 = new ArrayList<>();
//        amenities9.add(Amenity.NON_SMOKING_ROOMS);
//        amenities9.add(Amenity.PARKING);
//        amenities9.add(Amenity.RESTAURANT);
//
//        List<Amenity> amenities10 = new ArrayList<>();
//        amenities10.add(Amenity.FREE_WIFI);
//        amenities10.add(Amenity.FITNESS_CENTRE);
//
//        List<Photo> photos = new ArrayList<>();
//
//        Host host = new Host();
//
//        accommodations.add(new Accommodation("OceanViewEscape", "Indulge in panoramic ocean views from this luxurious coastal retreat. The three-bedroom villa features contemporary design, a fully equipped gourmet kitchen, and a private terrace for soaking in the sea breeze. Enjoy access to resort-style amenities, including a beachfront pool and spa. Ideal for a tranquil getaway or entertaining guests.", addresses.get(0),amenities1,photos,2,4, AccommodationType.HOTEL,allDateRanges.get(0), PriceType.PER_NIGHT, allPriceChanges.get(0),true, AccommodationStatus.CREATED,host, 200.0, 0.0, 7.8, 7));
////        accommodations.add(new Accommodation("Sky High Suite", "Elevate your stay in the luxurious Sky High Suite, situated on the top floors of a landmark skyscraper. This opulent three-bedroom suite offers panoramic city views through floor-to-ceiling windows, a private chef's kitchen, and a lavish entertainment lounge. Indulge in the epitome of sophistication and convenience in the heart of the urban skyline.", addresses.get(1),amenities2,photos,1,3,AccommodationType.HOSTEL,allDateRanges.get(1),PriceType.PER_GUEST,allPriceChanges.get(1),true,AccommodationStatus.ACTIVE,host, 50.0, 0.0, 8.0, 3));
//        accommodations.add(new Accommodation("City Lights Penthouse", "Experience the epitome of urban living in this breathtaking penthouse with floor-to-ceiling windows showcasing city lights. Boasting four bedrooms and a stylish modern interior, this high-rise oasis offers a fully stocked bar, entertainment lounge, and a private rooftop terrace with skyline views. Elevate your stay in the heart of the city.",addresses.get(2),amenities3,photos,4,8,AccommodationType.VILLA,allDateRanges.get(2),PriceType.PER_NIGHT, allPriceChanges.get(2),false,AccommodationStatus.CHANGED,host, 300.0,0.0,  9.0, 14));
////        accommodations.add(new Accommodation("Alpine Retreat Chalet", "Nestled in the serene alpine landscape, this chalet offers a cozy escape with log cabin charm. Featuring two bedrooms, a wood-burning fireplace, and a hot tub on the deck, it's an idyllic setting for a mountain retreat. Enjoy direct access to hiking trails and ski slopes for year-round outdoor adventures.", addresses.get(3),amenities4,photos,1,2,AccommodationType.APARTMENT,allDateRanges.get(3),PriceType.PER_NIGHT,allPriceChanges.get(3),false, AccommodationStatus.ACTIVE,host,80.0, 0.0, 9.5, 2));
////        accommodations.add(new Accommodation("Urban Retreat Suite", "Welcome to your new urban oasis! This stylish two-bedroom apartment boasts modern elegance with an open-concept design, featuring sleek hardwood floors and large windows that flood the space with natural light. The kitchen is a culinary enthusiast's dream, equipped with state-of-the-art stainless steel appliances and granite countertops. Unwind in the spacious living room, perfect for entertaining or cozy nights in, and retreat to the master bedroom with its en-suite bathroom for the ultimate in comfort. Located in a vibrant neighborhood, this apartment offers not just a home but a lifestyle, with trendy shops, restaurants, and parks just steps away.", addresses.get(4), amenities5, photos,2, 6, AccommodationType.RESORT,allDateRanges.get(4),PriceType.PER_NIGHT,allPriceChanges.get(4),true, AccommodationStatus.ACTIVE,host,500.0, 0.0,7.5,14));
////        accommodations.add(new Accommodation("Historic Inn Elegance", "Step back in time at this meticulously restored historic inn. Each room tells a story with antique furnishings and period decor. Indulge in a continental breakfast in the charming courtyard and explore nearby landmarks. Immerse yourself in the elegance of a bygone era while enjoying modern comforts.", addresses.get(5),amenities6, photos,2,4,AccommodationType.APARTMENT,allDateRanges.get(5),PriceType.PER_NIGHT,allPriceChanges.get(5),false,AccommodationStatus.CHANGED,host,120.0,0.0,8.8,7));
////        accommodations.add(new Accommodation("Skyline View Loft", "Perched atop a skyscraper, this contemporary loft offers unparalleled skyline views. The one-bedroom space features minimalist aesthetics, a fully equipped kitchen, and a spacious living area. Enjoy the luxury of a private balcony overlooking the city lights for a truly cosmopolitan experience.", addresses.get(6),amenities7,photos,6,10,AccommodationType.VILLA,allDateRanges.get(6),PriceType.PER_NIGHT,allPriceChanges.get(6),false,AccommodationStatus.ACTIVE,host, 450.0, 0.0,10.0, 14));
////        accommodations.add(new Accommodation("Tranquil Cottage Retreat", "Escape to the countryside in this charming cottage surrounded by lush gardens. The two-bedroom retreat features a rustic fireplace, a fully equipped kitchen, and a private patio for enjoying morning coffee or evening stargazing. Immerse yourself in the tranquility of nature while being just a short drive from local vineyards and hiking trails.", addresses.get(7), amenities8,photos,2,4,AccommodationType.HOTEL,allDateRanges.get(7),PriceType.PER_NIGHT,allPriceChanges.get(7),true,AccommodationStatus.ACTIVE,host,300.0, 0.0, 9.8, 7));
////        accommodations.add(new Accommodation("Beachfront Bungalow Paradise", "Experience paradise at this beachfront bungalow with direct access to the sandy shores. The tropical-themed two-bedroom haven offers a sun-soaked deck, hammocks for lazy afternoons, and stunning sunset views. Embrace the laid-back coastal lifestyle with surf lessons, beach picnics, and seaside relaxation.",addresses.get(8),amenities9,photos,1,3,AccommodationType.VILLA,allDateRanges.get(8),PriceType.PER_NIGHT,allPriceChanges.get(8),true, AccommodationStatus.ACTIVE,host,70.0,0.0, 8.4,3));
////        accommodations.add(new Accommodation("Riverside Retreat Cabin", "Unplug and unwind in this secluded cabin nestled by a serene river. The one-bedroom hideaway features a woodsy interior, a riverside deck for fishing or lounging, and a cozy fireplace. Disconnect from the hustle and bustle, and reconnect with nature in this peaceful riverside retreat.", addresses.get(9), amenities10,photos,2,4,AccommodationType.APARTMENT,allDateRanges.get(9),PriceType.PER_NIGHT,allPriceChanges.get(9),true,AccommodationStatus.ACTIVE,host, 90.0, 0.0, 8.7, 5));
//
//        return accommodations;
//    }
//
//    public static List<Reservation> generateReservations() {
//        List<Reservation> reservations = new ArrayList<>();
//
//        Guest guest =new Guest();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
//        List<Accommodation> accommodations = generateAccommodations();
//
//
//        try {
//            reservations.add(new Reservation(sdf.parse("2024-01-05 13:00:00.000000"), sdf.parse("2024-01-10 13:00:00.000000"), 300.0, 2, accommodations.get(0), guest, ReservationStatus.CREATED));
//            reservations.add(new Reservation(sdf.parse("2024-02-15 13:00:00.000000"), sdf.parse("2024-02-22 13:00:00.000000"), 450.0, 3, accommodations.get(1), guest, ReservationStatus.COMPLETED));
//            reservations.add(new Reservation(sdf.parse("2024-03-20 13:00:00.000000"), sdf.parse("2024-03-25 13:00:00.000000"), 200.0, 1, accommodations.get(2), guest, ReservationStatus.REJECTED));
//            reservations.add(new Reservation(sdf.parse("2024-04-05 13:00:00.000000"), sdf.parse("2024-04-15 13:00:00.000000"), 600.0, 4, accommodations.get(3), guest, ReservationStatus.ACCEPTED));
//            reservations.add(new Reservation(sdf.parse("2024-05-12 13:00:00.000000"), sdf.parse("2024-05-20 13:00:00.000000"), 350.0, 2, accommodations.get(4), guest, ReservationStatus.CANCELLED));
//            reservations.add(new Reservation(sdf.parse("2024-06-18 13:00:00.000000"), sdf.parse("2024-06-25 13:00:00.000000"), 700.0, 3, accommodations.get(5), guest, ReservationStatus.COMPLETED));
//            reservations.add(new Reservation(sdf.parse("2024-07-10 13:00:00.000000"), sdf.parse("2024-07-15 13:00:00.000000"), 250.0, 1, accommodations.get(6), guest, ReservationStatus.CREATED));
//            reservations.add(new Reservation(sdf.parse("2024-08-22 13:00:00.000000"), sdf.parse("2024-08-30 13:00:00.000000"), 500.0, 2, accommodations.get(7), guest, ReservationStatus.REJECTED));
//            reservations.add(new Reservation(sdf.parse("2024-09-05 13:00:00.000000"), sdf.parse("2024-09-15 13:00:00.000000"), 400.0, 3, accommodations.get(8), guest, ReservationStatus.REJECTED));
//            reservations.add(new Reservation(sdf.parse("2024-10-12 13:00:00.000000"), sdf.parse("2024-10-20 13:00:00.000000"), 800.0, 4, accommodations.get(9), guest, ReservationStatus.REJECTED));
//
//            reservations.add(new Reservation(sdf.parse("2024-11-18 13:00:00.000000"), sdf.parse("2024-11-25 13:00:00.000000"), 300.0, 2, accommodations.get(0), guest, ReservationStatus.CANCELLED));
//            reservations.add(new Reservation(sdf.parse("2024-12-15 13:00:00.000000"), sdf.parse("2025-01-02 13:00:00.000000"), 600.0, 3, accommodations.get(1), guest, ReservationStatus.COMPLETED));
//            reservations.add(new Reservation(sdf.parse("2025-01-20 13:00:00.000000"), sdf.parse("2025-02-01 13:00:00.000000"), 350.0, 2, accommodations.get(2), guest, ReservationStatus.CREATED));
//            reservations.add(new Reservation(sdf.parse("2025-02-15 13:00:00.000000"), sdf.parse("2025-02-22 13:00:00.000000"), 450.0, 3, accommodations.get(3), guest, ReservationStatus.COMPLETED));
//            reservations.add(new Reservation(sdf.parse("2025-03-20 13:00:00.000000"), sdf.parse("2025-03-25 13:00:00.000000"), 200.0, 1, accommodations.get(4), guest, ReservationStatus.REJECTED));
//            reservations.add(new Reservation(sdf.parse("2025-04-05 13:00:00.000000"), sdf.parse("2025-04-15 13:00:00.000000"), 700.0, 4, accommodations.get(5), guest, ReservationStatus.ACCEPTED));
//            reservations.add(new Reservation(sdf.parse("2025-05-12 13:00:00.000000"), sdf.parse("2025-05-20 13:00:00.000000"), 350.0, 2, accommodations.get(6), guest, ReservationStatus.CANCELLED));
//            reservations.add(new Reservation(sdf.parse("2025-06-18 13:00:00.000000"), sdf.parse("2025-06-25 13:00:00.000000"), 800.0, 3, accommodations.get(7), guest, ReservationStatus.COMPLETED));
//            reservations.add(new Reservation(sdf.parse("2025-07-10 13:00:00.000000"), sdf.parse("2025-07-15 13:00:00.000000"), 250.0, 1, accommodations.get(8), guest, ReservationStatus.CREATED));
//            reservations.add(new Reservation(sdf.parse("2025-08-22 13:00:00.000000"), sdf.parse("2025-08-30 13:00:00.000000"), 500.0, 2, accommodations.get(9), guest, ReservationStatus.ACCEPTED));
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return reservations;
//
//
//    }
//}
