package com.example.demo.activity;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public Activity getActivityById(Long id) {
        return activityRepository.findById(id).orElse(null);
    }

    public Activity updateActivity(Long id, Activity updatedActivity) {
        return activityRepository.findById(id)
                .map(activity -> {
                    activity.setActivityName(updatedActivity.getActivityName());
                    activity.setLocation(updatedActivity.getLocation());
                    activity.setStartTime(updatedActivity.getStartTime());
                    activity.setEndTime(updatedActivity.getEndTime());
                    activity.setCategory(updatedActivity.getCategory());
                    return activityRepository.save(activity);
                })
                .orElse(null);
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public List<Activity> getaActivitiesByItineraryId(Long itineraryId) {
        return activityRepository.findByItineraryId(itineraryId);
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }
}
