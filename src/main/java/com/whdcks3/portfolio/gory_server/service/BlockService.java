package com.whdcks3.portfolio.gory_server.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.whdcks3.portfolio.gory_server.data.dto.UserSimpleDto;
import com.whdcks3.portfolio.gory_server.data.models.Block;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.repositories.BlockRespository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRespository blockRespository;

    public void blockUser(User user, User other) {
        if (blockRespository.findByBlockerAndBlocked(user, other).isPresent()) {
            throw new IllegalStateException("이미 차단된 사용자입니다.");
        }
        if (user.getPid() == other.getPid()) {
            throw new IllegalStateException("자기 자신은 차단할 수 없습니다.");
        }

        Block block = new Block(user, other);
        blockRespository.save(block);
    }

    public void unblockUser(User user, User other) {
        Block block = blockRespository.findByBlockerAndBlocked(user, other)
                .orElseThrow(() -> new IllegalStateException("차단되어 있지 않습니다."));
        blockRespository.delete(block);
    }

    public List<UserSimpleDto> getBlockUsers(User user) {
        return blockRespository.findByBlocker(user).stream().map(block -> UserSimpleDto.toDto(block.getBlocked()))
                .toList();
    }

}
