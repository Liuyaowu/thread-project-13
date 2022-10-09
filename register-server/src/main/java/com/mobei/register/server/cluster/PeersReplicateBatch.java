package com.mobei.register.server.cluster;

import com.mobei.register.server.web.AbstractRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 集群同步batch
 */
public class PeersReplicateBatch {

    private List<AbstractRequest> requests = new ArrayList<>();

    public void add(AbstractRequest request) {
        this.requests.add(request);
    }

    public List<AbstractRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<AbstractRequest> requests) {
        this.requests = requests;
    }

}
