package gg.hound.core.disguise.api;

import java.util.HashMap;

public class Profile {
    String name;
    HashMap<Object, Object>[] properties;

    public DisguiseObject getDisguiseObject() {
        try {
            HashMap<Object, Object> map = this.properties[0];
            String value = map.get("value").toString();
            String signature = map.get("signature").toString();
            return new DisguiseObject(this.name, value, signature);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new DisguiseObject(null, "eyJ0aW1lc3RhbXAiOjE1MDg5MDE0MDkyNzQsInByb2ZpbGVJZCI6ImU1ZTM4NDVlM2U0ZTRlY2ZiMTIyMDVhNDRiMjJkZjRhIiwicHJvZmlsZU5hbWUiOiJhdHRlbnRpb25wbHoiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifSwidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYzE0MmVkMmZkZDUyMjdiNTg5NzM3Y2NiYWQwZmZmYmFjMWE5NTQ0ZWNkODFiOWNiM2NkYWRiMjQ2NzQifX19", "iA6FgXp9C8H7m40pnEXYWqxHWo76to3hh5aG7ajzEbXX6uIzmi+bU+8o+b/qhCbL0bSjKaH5IPP/JXE0ho0dG+ajfKNqsnZGjBMu1hXFg+e1ZcD2iBullrqCKWtAuBve9UFaY73v4F18DWpcVn93wbkdQ+BhlrdTsewKj7xQCwP8p0nNDREViHRZIBGdBv5uRB+JG0cid6zU2oHYoHtX/l0sw6SBxQO3zZE/NRS5czoWLjfmHY2z73/Xftu68WMmrIBhK76A7Y74aThYlC4oh/05V0151JMLuROwI11MspJHNHynJIKYZ40LuItf39N4l/IRv3pDJv1G0A44gcfQwcEytmms4QlT8hW4/Zt3QDLLXkvdx1XXbwnxvXUoikZfoqQM5KV3LkQDZ4D6SbLuMnzbDWIQfmmWolXIdJnjIHis6KMHuVbJkZYFT6yfGAlYv/QDOB5dV/gtvN18aFtbUZHYPxo70g4FQR6Iky2vw1XSVmvM7hDWWVYzHti7DWQStPsPMyK0LfS/CO1t8BLcZ+fQjSCH+opsSKl9E276MqLWSykadk+q3cZbdV6AGnrb1NT2eima2fJI3OXXn/yssaNghe+jzVfogj/rR5XRiYO1eH7GjTGTGKaK508UzMdvfDfiRqZ6VCXzP0WLTBsuRTGPsCqV3glxzP+cHlq9fgU=");
    }
}
