package com.whdcks3.portfolio.gory_server.service.interfaces;

import com.whdcks3.portfolio.gory_server.data.dto.SquadSimpleDto;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;

public interface ISquadService {
    SquadSimpleDto createSquad(User user, SquadRequest req);

    void modifySquad(User user, Long sid, SquadRequest req);

    void deleteSquad(User user, Long sid, boolean isForcedDelete);

    void joinSquad(User user, Squad squad);

    void closeSquad(User user, Long squadId);
}
