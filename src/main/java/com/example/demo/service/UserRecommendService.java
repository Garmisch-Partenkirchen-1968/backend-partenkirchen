package com.example.demo.service;

import com.example.demo.dto.user.Fixer;
import com.example.demo.entity.Project;
import com.example.demo.entity.User;
import com.example.demo.entity.enumerate.IssuePriority;
import com.example.demo.repository.IssueRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.example.demo.service.ProjectService.getFixerList;

@Service
@RequiredArgsConstructor
public class UserRecommendService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public List<Fixer> getRecommendedAssigneeList(Long projectId, Long userId, IssuePriority priority) {
        Project project = getProject(projectId);
        User user = getUser(userId);

        // user가 project member가 아닌 경우
        if(project.getMembers().get(user) == null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this project");
        }

        List<Fixer> fixers = getFixerList(project);

        fixers.sort(Comparator
                .comparing((Fixer f) -> f.getPriority() == priority ? 0 : 1)
                .thenComparing(Comparator.comparing(Fixer::getNumberOfFixed).reversed())
        );

        Set<String> seen = new HashSet<>();
        List<Fixer> uniqueFixers = new ArrayList<>();
        for (Fixer fixer : fixers) {
            if (seen.add(fixer.getUsername())) {
                uniqueFixers.add(fixer);
            }
        }

        return uniqueFixers;
    }

    private Project getProject(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "project not found");
        }
        return optionalProject.get();
    }

    private User getUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user not found");
        }
        return optionalUser.get();
    }
}
