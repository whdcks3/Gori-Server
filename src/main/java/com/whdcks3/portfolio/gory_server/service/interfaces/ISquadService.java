package com.whdcks3.portfolio.gory_server.service.interfaces;

import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;

public interface ISquadService {
    void createSquad(User user, SquadRequest req);

    void modifySquad(Long uid, Long sid, SquadRequest req);

    void deleteSquad(User user, Long sid, boolean isForcedDelete);

    void joinSquad(User user, Long sqaudId);
}
