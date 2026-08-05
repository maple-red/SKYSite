package com.skyl1n.portal.service;

import com.skyl1n.portal.entity.Message;
import com.skyl1n.portal.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAllByOrderByCreatedAtDesc();
    }

    public Message createMessage(String name, String content) {
        Message msg = new Message();
        msg.setName((name == null || name.trim().isEmpty()) ? "匿名" : name.trim());
        msg.setContent(content.trim());
        return messageRepository.save(msg);
    }

    public boolean deleteMessage(Long id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
