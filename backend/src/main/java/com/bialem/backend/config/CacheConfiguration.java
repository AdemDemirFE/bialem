package com.bialem.backend.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.bialem.backend.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.bialem.backend.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.bialem.backend.domain.User.class.getName());
            createCache(cm, com.bialem.backend.domain.Authority.class.getName());
            createCache(cm, com.bialem.backend.domain.User.class.getName() + ".authorities");
            createCache(cm, com.bialem.backend.domain.Profile.class.getName());
            createCache(cm, com.bialem.backend.domain.AccountPreferences.class.getName());
            createCache(cm, com.bialem.backend.domain.Community.class.getName());
            createCache(cm, com.bialem.backend.domain.Community.class.getName() + ".children");
            createCache(cm, com.bialem.backend.domain.Community.class.getName() + ".categorizedGroups");
            createCache(cm, com.bialem.backend.domain.Community.class.getName() + ".members");
            createCache(cm, com.bialem.backend.domain.Community.class.getName() + ".assistants");
            createCache(cm, com.bialem.backend.domain.Community.class.getName() + ".events");
            createCache(cm, com.bialem.backend.domain.Community.class.getName() + ".posts");
            createCache(cm, com.bialem.backend.domain.Community.class.getName() + ".storyTargets");
            createCache(cm, com.bialem.backend.domain.CommunityMember.class.getName());
            createCache(cm, com.bialem.backend.domain.CommunityModeratorAssistant.class.getName());
            createCache(cm, com.bialem.backend.domain.Event.class.getName());
            createCache(cm, com.bialem.backend.domain.Event.class.getName() + ".participants");
            createCache(cm, com.bialem.backend.domain.Event.class.getName() + ".messages");
            createCache(cm, com.bialem.backend.domain.Event.class.getName() + ".ratings");
            createCache(cm, com.bialem.backend.domain.Event.class.getName() + ".posts");
            createCache(cm, com.bialem.backend.domain.EventParticipant.class.getName());
            createCache(cm, com.bialem.backend.domain.EventMessage.class.getName());
            createCache(cm, com.bialem.backend.domain.EventRating.class.getName());
            createCache(cm, com.bialem.backend.domain.Post.class.getName());
            createCache(cm, com.bialem.backend.domain.Post.class.getName() + ".media");
            createCache(cm, com.bialem.backend.domain.PostMedia.class.getName());
            createCache(cm, com.bialem.backend.domain.Comment.class.getName());
            createCache(cm, com.bialem.backend.domain.Story.class.getName());
            createCache(cm, com.bialem.backend.domain.Story.class.getName() + ".views");
            createCache(cm, com.bialem.backend.domain.Story.class.getName() + ".communityTargets");
            createCache(cm, com.bialem.backend.domain.StoryView.class.getName());
            createCache(cm, com.bialem.backend.domain.StoryCommunityTarget.class.getName());
            createCache(cm, com.bialem.backend.domain.Follow.class.getName());
            createCache(cm, com.bialem.backend.domain.FollowRequest.class.getName());
            createCache(cm, com.bialem.backend.domain.Block.class.getName());
            createCache(cm, com.bialem.backend.domain.UserReview.class.getName());
            createCache(cm, com.bialem.backend.domain.Report.class.getName());
            createCache(cm, com.bialem.backend.domain.Notification.class.getName());
            createCache(cm, com.bialem.backend.domain.PushToken.class.getName());
            createCache(cm, com.bialem.backend.domain.CityEvent.class.getName());
            createCache(cm, com.bialem.backend.domain.CityEvent.class.getName() + ".interests");
            createCache(cm, com.bialem.backend.domain.CityEvent.class.getName() + ".ticketOffers");
            createCache(cm, com.bialem.backend.domain.CityEventInterest.class.getName());
            createCache(cm, com.bialem.backend.domain.CityEventTicketOffer.class.getName());
            createCache(cm, com.bialem.backend.domain.CityEventSyncLog.class.getName());
            createCache(cm, com.bialem.backend.domain.PartnerVenue.class.getName());
            createCache(cm, com.bialem.backend.domain.PartnerVenue.class.getName() + ".offers");
            createCache(cm, com.bialem.backend.domain.PartnerVenue.class.getName() + ".staff");
            createCache(cm, com.bialem.backend.domain.PartnerOffer.class.getName());
            createCache(cm, com.bialem.backend.domain.PartnerOffer.class.getName() + ".redemptions");
            createCache(cm, com.bialem.backend.domain.PartnerVenueStaff.class.getName());
            createCache(cm, com.bialem.backend.domain.PartnerOfferRedemption.class.getName());
            createCache(cm, com.bialem.backend.domain.HonorBadge.class.getName());
            createCache(cm, com.bialem.backend.domain.UserHonorBadge.class.getName());
            createCache(cm, com.bialem.backend.domain.AiUsageLog.class.getName());
            createCache(cm, com.bialem.backend.domain.PlatformTeamMember.class.getName());
            createCache(cm, com.bialem.backend.domain.Role.class.getName());
            createCache(cm, com.bialem.backend.domain.UserRole.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
