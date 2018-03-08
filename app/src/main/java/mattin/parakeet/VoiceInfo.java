package mattin.parakeet;

/**
 * Created by Mattin on 3/7/2018.
 */

public class VoiceInfo {
    private final String id;
    private final String name;
    private final String gender;
    private final String language;

    public VoiceInfo(String id, String name, String gender, String language) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getLanguage() {
        return language;
    }
}
