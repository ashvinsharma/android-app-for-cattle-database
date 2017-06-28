package in.nic.phra.app.data;

/**
 * Created by ashvi on 28-06-2017.
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
