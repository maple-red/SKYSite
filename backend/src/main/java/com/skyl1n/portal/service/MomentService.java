package com.skyl1n.portal.service;

import com.skyl1n.portal.entity.Moment;
import com.skyl1n.portal.entity.MomentLike;
import com.skyl1n.portal.repository.MomentRepository;
import com.skyl1n.portal.repository.MomentLikeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MomentService {

    private final MomentRepository momentRepository;
    private final MomentLikeRepository momentLikeRepository;

    public MomentService(MomentRepository momentRepository,
                         MomentLikeRepository momentLikeRepository) {
        this.momentRepository = momentRepository;
        this.momentLikeRepository = momentLikeRepository;
    }

    public List<Map<String, Object>> getAllMoments(String currentSessionId) {
        List<Moment> moments = momentRepository.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Moment m : moments) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", m.getId());
            item.put("content", m.getContent());
            item.put("likesCount", m.getLikesCount());
            item.put("createdAt", m.getCreatedAt());

            boolean liked = currentSessionId != null &&
                momentLikeRepository.findByMomentIdAndSessionId(m.getId(), currentSessionId).isPresent();
            item.put("liked", liked);
            result.add(item);
        }
        return result;
    }

    public Moment createMoment(String content) {
        Moment m = new Moment();
        m.setContent(content.trim());
        return momentRepository.save(m);
    }

    public boolean deleteMoment(Long id) {
        if (momentRepository.existsById(id)) {
            momentLikeRepository.findByMomentIdAndSessionId(id, null); // no-op
            momentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Map<String, Object> toggleLike(Long momentId, String sessionId) {
        Optional<Moment> momentOpt = momentRepository.findById(momentId);
        if (momentOpt.isEmpty()) {
            return null;
        }
        Moment moment = momentOpt.get();
        Optional<MomentLike> existing = momentLikeRepository.findByMomentIdAndSessionId(momentId, sessionId);

        if (existing.isPresent()) {
            momentLikeRepository.delete(existing.get());
            moment.setLikesCount(moment.getLikesCount() - 1);
        } else {
            MomentLike like = new MomentLike();
            like.setMomentId(momentId);
            like.setSessionId(sessionId);
            momentLikeRepository.save(like);
            moment.setLikesCount(moment.getLikesCount() + 1);
        }
        momentRepository.save(moment);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("likesCount", moment.getLikesCount());
        result.put("liked", existing.isEmpty());
        return result;
    }
}
