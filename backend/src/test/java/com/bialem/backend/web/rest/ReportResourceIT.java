package com.bialem.backend.web.rest;

import static com.bialem.backend.domain.ReportAsserts.*;
import static com.bialem.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bialem.backend.IntegrationTest;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Report;
import com.bialem.backend.domain.enumeration.ReportStatus;
import com.bialem.backend.domain.enumeration.ReportTargetType;
import com.bialem.backend.repository.ReportRepository;
import com.bialem.backend.service.dto.ReportDTO;
import com.bialem.backend.service.mapper.ReportMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ReportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReportResourceIT {

    private static final ReportTargetType DEFAULT_TARGET_TYPE = ReportTargetType.POST;
    private static final ReportTargetType UPDATED_TARGET_TYPE = ReportTargetType.COMMENT;

    private static final String DEFAULT_TARGET_ID = "target-1";
    private static final String UPDATED_TARGET_ID = "target-2";

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_DETAILS = "AAAAAAAAAA";
    private static final String UPDATED_DETAILS = "BBBBBBBBBB";

    private static final ReportStatus DEFAULT_STATUS = ReportStatus.OPEN;
    private static final ReportStatus UPDATED_STATUS = ReportStatus.UNDER_REVIEW;

    private static final Instant DEFAULT_RESOLVED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RESOLVED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReportMockMvc;

    private Report report;

    private Report insertedReport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createEntity() {
        return new Report()
            .targetType(DEFAULT_TARGET_TYPE)
            .targetId(DEFAULT_TARGET_ID)
            .reason(DEFAULT_REASON)
            .details(DEFAULT_DETAILS)
            .status(DEFAULT_STATUS)
            .resolvedAt(DEFAULT_RESOLVED_AT)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createUpdatedEntity() {
        return new Report()
            .targetType(UPDATED_TARGET_TYPE)
            .targetId(UPDATED_TARGET_ID)
            .reason(UPDATED_REASON)
            .details(UPDATED_DETAILS)
            .status(UPDATED_STATUS)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        report = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedReport != null) {
            reportRepository.delete(insertedReport);
            insertedReport = null;
        }
    }

    @Test
    @Transactional
    void createReport() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);
        var returnedReportDTO = om.readValue(
            restReportMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReportDTO.class
        );

        // Validate the Report in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReport = reportMapper.toEntity(returnedReportDTO);
        assertReportUpdatableFieldsEquals(returnedReport, getPersistedReport(returnedReport));

        insertedReport = returnedReport;
    }

    @Test
    @Transactional
    void createReportWithExistingId() throws Exception {
        // Create the Report with an existing ID
        report.setId(1L);
        ReportDTO reportDTO = reportMapper.toDto(report);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTargetTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        report.setTargetType(null);

        // Create the Report, which fails.
        ReportDTO reportDTO = reportMapper.toDto(report);

        restReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTargetIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        report.setTargetId(null);

        // Create the Report, which fails.
        ReportDTO reportDTO = reportMapper.toDto(report);

        restReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        report.setReason(null);

        // Create the Report, which fails.
        ReportDTO reportDTO = reportMapper.toDto(report);

        restReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        report.setStatus(null);

        // Create the Report, which fails.
        ReportDTO reportDTO = reportMapper.toDto(report);

        restReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        report.setCreatedAt(null);

        // Create the Report, which fails.
        ReportDTO reportDTO = reportMapper.toDto(report);

        restReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        report.setUpdatedAt(null);

        // Create the Report, which fails.
        ReportDTO reportDTO = reportMapper.toDto(report);

        restReportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReports() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList
        restReportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
            .andExpect(jsonPath("$.[*].targetType").value(hasItem(DEFAULT_TARGET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].targetId").value(hasItem(DEFAULT_TARGET_ID.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].resolvedAt").value(hasItem(DEFAULT_RESOLVED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getReport() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc
            .perform(get(ENTITY_API_URL_ID, report.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(report.getId().intValue()))
            .andExpect(jsonPath("$.targetType").value(DEFAULT_TARGET_TYPE.toString()))
            .andExpect(jsonPath("$.targetId").value(DEFAULT_TARGET_ID.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.details").value(DEFAULT_DETAILS))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.resolvedAt").value(DEFAULT_RESOLVED_AT.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getReportsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        Long id = report.getId();

        defaultReportFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReportFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReportFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReportsByTargetTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where targetType equals to
        defaultReportFiltering("targetType.equals=" + DEFAULT_TARGET_TYPE, "targetType.equals=" + UPDATED_TARGET_TYPE);
    }

    @Test
    @Transactional
    void getAllReportsByTargetTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where targetType in
        defaultReportFiltering("targetType.in=" + DEFAULT_TARGET_TYPE + "," + UPDATED_TARGET_TYPE, "targetType.in=" + UPDATED_TARGET_TYPE);
    }

    @Test
    @Transactional
    void getAllReportsByTargetTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where targetType is not null
        defaultReportFiltering("targetType.specified=true", "targetType.specified=false");
    }

    @Test
    @Transactional
    void getAllReportsByTargetIdIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where targetId equals to
        defaultReportFiltering("targetId.equals=" + DEFAULT_TARGET_ID, "targetId.equals=" + UPDATED_TARGET_ID);
    }

    @Test
    @Transactional
    void getAllReportsByTargetIdIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where targetId in
        defaultReportFiltering("targetId.in=" + DEFAULT_TARGET_ID + "," + UPDATED_TARGET_ID, "targetId.in=" + UPDATED_TARGET_ID);
    }

    @Test
    @Transactional
    void getAllReportsByTargetIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where targetId is not null
        defaultReportFiltering("targetId.specified=true", "targetId.specified=false");
    }

    @Test
    @Transactional
    void getAllReportsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where reason equals to
        defaultReportFiltering("reason.equals=" + DEFAULT_REASON, "reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllReportsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where reason in
        defaultReportFiltering("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON, "reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllReportsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where reason is not null
        defaultReportFiltering("reason.specified=true", "reason.specified=false");
    }

    @Test
    @Transactional
    void getAllReportsByReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where reason contains
        defaultReportFiltering("reason.contains=" + DEFAULT_REASON, "reason.contains=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllReportsByReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where reason does not contain
        defaultReportFiltering("reason.doesNotContain=" + UPDATED_REASON, "reason.doesNotContain=" + DEFAULT_REASON);
    }

    @Test
    @Transactional
    void getAllReportsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where status equals to
        defaultReportFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReportsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where status in
        defaultReportFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReportsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where status is not null
        defaultReportFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllReportsByResolvedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where resolvedAt equals to
        defaultReportFiltering("resolvedAt.equals=" + DEFAULT_RESOLVED_AT, "resolvedAt.equals=" + UPDATED_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllReportsByResolvedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where resolvedAt in
        defaultReportFiltering("resolvedAt.in=" + DEFAULT_RESOLVED_AT + "," + UPDATED_RESOLVED_AT, "resolvedAt.in=" + UPDATED_RESOLVED_AT);
    }

    @Test
    @Transactional
    void getAllReportsByResolvedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where resolvedAt is not null
        defaultReportFiltering("resolvedAt.specified=true", "resolvedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReportsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where createdAt equals to
        defaultReportFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllReportsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where createdAt in
        defaultReportFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllReportsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where createdAt is not null
        defaultReportFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReportsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where updatedAt equals to
        defaultReportFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllReportsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where updatedAt in
        defaultReportFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllReportsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList where updatedAt is not null
        defaultReportFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllReportsByReporterIsEqualToSomething() throws Exception {
        Profile reporter;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            reportRepository.saveAndFlush(report);
            reporter = ProfileResourceIT.createEntity(em);
        } else {
            reporter = TestUtil.findAll(em, Profile.class).get(0);
        }
        em.persist(reporter);
        em.flush();
        report.setReporter(reporter);
        reportRepository.saveAndFlush(report);
        Long reporterId = reporter.getId();
        // Get all the reportList where reporter equals to reporterId
        defaultReportShouldBeFound("reporterId.equals=" + reporterId);

        // Get all the reportList where reporter equals to (reporterId + 1)
        defaultReportShouldNotBeFound("reporterId.equals=" + (reporterId + 1));
    }

    @Test
    @Transactional
    void getAllReportsByResolvedByIsEqualToSomething() throws Exception {
        Profile resolvedBy;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            reportRepository.saveAndFlush(report);
            resolvedBy = ProfileResourceIT.createEntity(em);
        } else {
            resolvedBy = TestUtil.findAll(em, Profile.class).get(0);
        }
        em.persist(resolvedBy);
        em.flush();
        report.setResolvedBy(resolvedBy);
        reportRepository.saveAndFlush(report);
        Long resolvedById = resolvedBy.getId();
        // Get all the reportList where resolvedBy equals to resolvedById
        defaultReportShouldBeFound("resolvedById.equals=" + resolvedById);

        // Get all the reportList where resolvedBy equals to (resolvedById + 1)
        defaultReportShouldNotBeFound("resolvedById.equals=" + (resolvedById + 1));
    }

    private void defaultReportFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReportShouldBeFound(shouldBeFound);
        defaultReportShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReportShouldBeFound(String filter) throws Exception {
        restReportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
            .andExpect(jsonPath("$.[*].targetType").value(hasItem(DEFAULT_TARGET_TYPE.toString())))
            .andExpect(jsonPath("$.[*].targetId").value(hasItem(DEFAULT_TARGET_ID.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].details").value(hasItem(DEFAULT_DETAILS)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].resolvedAt").value(hasItem(DEFAULT_RESOLVED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restReportMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReportShouldNotBeFound(String filter) throws Exception {
        restReportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReportMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReport() throws Exception {
        // Get the report
        restReportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReport() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the report
        Report updatedReport = reportRepository.findById(report.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReport are not directly saved in db
        em.detach(updatedReport);
        updatedReport
            .targetType(UPDATED_TARGET_TYPE)
            .targetId(UPDATED_TARGET_ID)
            .reason(UPDATED_REASON)
            .details(UPDATED_DETAILS)
            .status(UPDATED_STATUS)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        ReportDTO reportDTO = reportMapper.toDto(updatedReport);

        restReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reportDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO))
            )
            .andExpect(status().isOk());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReportToMatchAllProperties(updatedReport);
    }

    @Test
    @Transactional
    void putNonExistingReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        report.setId(longCount.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, reportDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        report.setId(longCount.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        report.setId(longCount.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(reportDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReportWithPatch() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the report using partial update
        Report partialUpdatedReport = new Report();
        partialUpdatedReport.setId(report.getId());

        partialUpdatedReport.targetId(UPDATED_TARGET_ID).details(UPDATED_DETAILS).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReport))
            )
            .andExpect(status().isOk());

        // Validate the Report in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReportUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedReport, report), getPersistedReport(report));
    }

    @Test
    @Transactional
    void fullUpdateReportWithPatch() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the report using partial update
        Report partialUpdatedReport = new Report();
        partialUpdatedReport.setId(report.getId());

        partialUpdatedReport
            .targetType(UPDATED_TARGET_TYPE)
            .targetId(UPDATED_TARGET_ID)
            .reason(UPDATED_REASON)
            .details(UPDATED_DETAILS)
            .status(UPDATED_STATUS)
            .resolvedAt(UPDATED_RESOLVED_AT)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReport))
            )
            .andExpect(status().isOk());

        // Validate the Report in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReportUpdatableFieldsEquals(partialUpdatedReport, getPersistedReport(partialUpdatedReport));
    }

    @Test
    @Transactional
    void patchNonExistingReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        report.setId(longCount.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, reportDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        report.setId(longCount.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(reportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        report.setId(longCount.incrementAndGet());

        // Create the Report
        ReportDTO reportDTO = reportMapper.toDto(report);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(reportDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReport() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the report
        restReportMockMvc
            .perform(delete(ENTITY_API_URL_ID, report.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return reportRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Report getPersistedReport(Report report) {
        return reportRepository.findById(report.getId()).orElseThrow();
    }

    protected void assertPersistedReportToMatchAllProperties(Report expectedReport) {
        assertReportAllPropertiesEquals(expectedReport, getPersistedReport(expectedReport));
    }

    protected void assertPersistedReportToMatchUpdatableProperties(Report expectedReport) {
        assertReportAllUpdatablePropertiesEquals(expectedReport, getPersistedReport(expectedReport));
    }
}
