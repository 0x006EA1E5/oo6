package org.otherobjects.cms.ws;

import java.util.List;

import del.icio.us.Delicious;
import del.icio.us.beans.Post;

public class DeliciousLinkService
{
    // FIXME Extract this to config
    private static Delicious delicious = new Delicious("raston", "6n5s67@n!^^656");

    @SuppressWarnings("unchecked")
    public List<Post> getLinks()
    {
        return delicious.getRecentPosts();
    }

    public Object getLink(String id)
    {
        return delicious.getPostForURL(id);
    }

    public Object postLink(String id, String title)
    {
        return delicious.getPostForURL(id);
    }

}
