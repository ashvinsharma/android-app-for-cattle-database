package in.nic.phra.app.data;

/**
 * @deprecated
 * Created by Ashvin Sharma <ashvinsharma97@gmail.com> on 28-06-2017 .
 */

public class Breed {
    private String breed;
    private int breedID;

    public Breed(String breed, int breedID) {
        this.breed = breed;
        this.breedID = breedID;
    }

    public String getBreed() {
        return breed;
    }

    public int getBreedID() {
        return breedID;
    }
}
