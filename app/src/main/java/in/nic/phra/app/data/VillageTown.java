package in.nic.phra.app.data;

/**
 * @deprecated
 * Created by ashvi on 27-06-2017.
 * stores for moving data(village name and ID) from AsyncTask to OwnerForm Class
 */

public class VillageTown {
    private String name;
    private int ID;

    public VillageTown(String name, int ID) {
        this.name = name;
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

}
