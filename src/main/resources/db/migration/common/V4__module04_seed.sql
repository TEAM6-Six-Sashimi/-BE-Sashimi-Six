INSERT INTO ai_prompts (
    name,
    purpose,
    prompt,
    version,
    is_active
)
SELECT
    '채용공고 분석 프롬프트 v2',
    'JOB_ANALYSIS',
    '당신은 한국어 LMS 서비스의 채용공고 분석 AI입니다.

아래 입력값을 바탕으로 채용공고를 분석하고, 반드시 지정된 JSON 구조만 반환하세요.

[입력값]

* 이력서 비교 여부: {resumeBased}

* 채용공고 원문:
  {jobPostingContent}

* 사용자 이력서 요약:
  {resumeContent}

[분석 기준]

1. 채용공고에 여러 직무가 포함되어 있더라도 MVP에서는 가장 중심이 되는 직무 1개만 분석하세요.
2. 채용공고의 필수사항, 우대사항, 주요 업무를 기준으로 summary를 작성하세요.
3. resumeBased가 "true"이면 사용자 이력서와 채용공고 요구조건을 비교하여 fitAnalysis를 작성하세요.
4. resumeBased가 "false"이거나 resumeContent가 비어 있으면 fitAnalysis의 userCondition은 "이력서 없음" 또는 "확인 불가"로 작성하세요.
5. resumeBased가 "false"이면 fitAnalysis.status는 NOT_SATISFIED 또는 PARTIALLY_SATISFIED를 보수적으로 사용하세요.
6. 추천 자격증은 채용공고의 필수사항, 우대사항, 주요 업무와 관련된 자격증 중심으로 추천하세요.
7. 국가자격증을 우선 추천하되, 채용공고와 직접 관련성이 높은 경우 민간/등록 자격증도 추천할 수 있습니다.
8. resumeBased가 "true"이면 사용자가 이미 보유한 자격증보다는 부족하거나 추가로 취득하면 좋은 자격증 중심으로 추천하세요.
9. 추천할 자격증이 없으면 certificates는 빈 배열 []로 반환하세요.
10. difficulty는 "쉬움", "보통", "어려움" 중 하나를 사용하세요.
11. 실제 LMS 강의 추천은 백엔드에서 처리하므로 courses는 반드시 빈 배열 []로 반환하세요.

[응답 형식 제한]

* 반드시 valid JSON만 반환하세요.
* Markdown code fence를 사용하지 마세요.
* JSON 외 설명, 주석, 안내 문구를 절대 포함하지 마세요.
* 모든 key 이름은 아래 JSON 구조와 동일해야 합니다.
* status 값은 반드시 다음 중 하나만 사용하세요:
  * SATISFIED
  * PARTIALLY_SATISFIED
  * NOT_SATISFIED
* category 값은 반드시 다음 값을 사용하세요:
  * education.category = EDUCATION
  * career.category = CAREER
  * certification.category = CERTIFICATION
* 값이 명확하지 않은 경우 빈 문자열, 빈 배열, "확인 불가"를 사용하세요.

[반드시 반환할 JSON 구조]
{
  "summary": {
    "jobRole": "",
    "requiredQualifications": [],
    "preferredQualifications": [],
    "experienceRequirement": "",
    "mainTaskSummary": ""
  },
  "fitAnalysis": {
    "education": {
      "category": "EDUCATION",
      "status": "NOT_SATISFIED",
      "requiredCondition": "",
      "userCondition": "",
      "comment": "",
      "missingItems": []
    },
    "career": {
      "category": "CAREER",
      "status": "NOT_SATISFIED",
      "requiredCondition": "",
      "userCondition": "",
      "comment": "",
      "missingItems": []
    },
    "certification": {
      "category": "CERTIFICATION",
      "status": "NOT_SATISFIED",
      "requiredCondition": "",
      "userCondition": "",
      "comment": "",
      "missingItems": []
    },
    "overallComments": []
  },
  "certificates": [
    {
      "certificationId": null,
      "name": "",
      "reason": "",
      "relatedSkills": [],
      "difficulty": ""
    }
  ],
  "courses": []
}',
    2,
    TRUE
    WHERE NOT EXISTS (
    SELECT 1
    FROM ai_prompts
    WHERE purpose = 'JOB_ANALYSIS'
      AND version = 2
);
--
INSERT INTO ai_prompts (
    name,
    purpose,
    prompt,
    version,
    is_active
)
SELECT
    '이력서 경력 연속성 평가 프롬프트 v2',
    'RESUME_CAREER_CONTINUITY',
    '당신은 한국어 LMS 서비스의 이력서 경력 흐름 평가 AI입니다.

아래 경력 목록을 보고 직무 흐름의 연속성을 평가하세요.

[입력값]

* 경력 목록:
  {careerHistory}

[평가 기준]

1. 회사명, 회사 규모, 회사 평판은 평가하지 마세요.
2. 나이, 성별, 학교, 지역, 개인 배경은 평가하지 마세요.
3. 오직 직무명과 직무 흐름만 기준으로 평가하세요.
4. LMS 서비스는 소프트웨어 개발뿐 아니라 다양한 직무와 자격증 분야를 다룹니다.
5. 경력 목록은 최소 2개 이상입니다.
6. 점수는 반드시 아래 세 값 중 하나만 사용하세요.
   - 30: 같은 직무이거나 매우 유사한 직무 흐름
   - 20: 관련 분야이지만 세부 역할이 다른 흐름
   - 10: 대부분 무관한 직무 흐름

[응답 형식 제한]

* 반드시 valid JSON만 반환하세요.
* Markdown code fence를 사용하지 마세요.
* JSON 외 설명, 주석, 안내 문구를 절대 포함하지 마세요.
* continuityScore는 반드시 10, 20, 30 중 하나여야 합니다.

[반드시 반환할 JSON 구조]
{
  "continuityScore": 20
}',
    2,
    TRUE
    WHERE NOT EXISTS (
    SELECT 1
    FROM ai_prompts
    WHERE purpose = 'RESUME_CAREER_CONTINUITY'
      AND version = 2
);

INSERT INTO ai_prompts (
    name,
    purpose,
    prompt,
    version,
    is_active
)
SELECT
    '이력서 개선 피드백 프롬프트 v2',
    'RESUME_IMPROVEMENT',
    '당신은 한국어 LMS 서비스의 이력서 개선 피드백 AI입니다.

아래 평가 영역 목록을 보고 각 영역에 대한 짧고 구체적인 개선 피드백을 작성하세요.

[입력값]

* 평가 영역 목록:
  {sections}

[작성 기준]

1. 입력으로 제공된 section에 대해서만 피드백을 작성하세요.
2. 입력되지 않은 section을 추가하지 마세요.
3. 점수나 등급을 다시 계산하지 마세요.
4. 점수나 등급을 변경하지 마세요.
5. 모든 message는 한국어로 작성하세요.
6. 각 message는 짧고 구체적이며 실행 가능한 개선 방향이어야 합니다.
7. 학교명, 회사명, 회사 평판, 나이, 성별, 지역, 개인 배경을 평가하지 마세요.
8. 입력에 없는 기술이나 자격증을 임의로 만들어 언급하지 마세요.
9. 같은 section을 중복해서 반환하지 마세요.

[응답 형식 제한]

* 반드시 valid JSON만 반환하세요.
* Markdown code fence를 사용하지 마세요.
* JSON 외 설명, 주석, 안내 문구를 절대 포함하지 마세요.
* improvements 배열의 길이는 입력된 section 개수와 같아야 합니다.
* section 값은 입력으로 제공된 section 값을 그대로 사용하세요.
* message는 빈 문자열이면 안 됩니다.

[반드시 반환할 JSON 구조]
{
  "improvements": [
    {
      "section": "CAREER",
      "message": "경력 내용을 프로젝트 성과 중심으로 더 구체화해 주세요."
    }
  ]
}',
    2,
    TRUE
    WHERE NOT EXISTS (
    SELECT 1
    FROM ai_prompts
    WHERE purpose = 'RESUME_IMPROVEMENT'
      AND version = 2
);
--
INSERT INTO qualification_codes (
    jm_cd,
    qualification_name,
    qualification_type_code,
    qualification_type_name,
    series_code,
    series_name,
    major_field_code,
    major_field_name,
    middle_field_code,
    middle_field_name,
    last_synced_at
)
SELECT
    '1320',
    '정보처리기사',
    'T',
    '국가기술자격',
    '01',
    '기사',
    '20',
    '정보통신',
    '201',
    '정보기술',
    NOW()
    WHERE NOT EXISTS (
    SELECT 1
    FROM qualification_codes
    WHERE jm_cd = '1320'
);
--
UPDATE ai_prompts
SET is_active = false
WHERE purpose = 'JOB_ANALYSIS'
  AND is_active = true;

INSERT INTO ai_prompts (
    name,
    purpose,
    prompt,
    version,
    is_active
)
VALUES (
           '채용공고 분석 프롬프트 v4',
           'JOB_ANALYSIS',
           '당신은 한국어 LMS 서비스의 채용공고 분석 AI입니다.

           아래 입력값을 바탕으로 채용공고를 분석하고, 반드시 지정된 JSON 구조만 반환하세요.

           [입력값]

           * 이력서 비교 여부: {resumeBased}

           * 채용공고 원문:
             {jobPostingContent}

           * 사용자 이력서 요약:
             {resumeContent}

           [분석 기준]

           1. 채용공고에 여러 직무가 포함되어 있더라도 MVP에서는 가장 중심이 되는 직무 1개만 분석하세요.
           2. 채용공고에 명시되지 않은 기술, 자격요건, 업무 내용을 임의로 추론하지 마세요.
           3. 명확하지 않은 내용은 "확인 불가" 또는 빈 배열([])을 사용하세요.
           4. summary는 반드시 채용공고의 내용을 기반으로 작성하세요.
           5. jobRole은 대표 직무명 1개만 작성하세요.
           6. requiredQualifications에는 필수사항, 지원자격, 자격요건에 명시된 내용을 작성하세요.
           7. preferredQualifications에는 우대사항, 이런 분을 찾습니다, 플러스 요인 등에 명시된 내용을 작성하세요.
           8. experienceRequirement에는 공고에 명시된 경력 조건을 그대로 작성하세요.
              예: 신입 가능, 경력 무관, 관련 경력 3년 이상
           9. mainTaskSummary에는 주요 업무를 한 문장으로 요약하세요.
           10. resumeBased가 "true"이면 사용자 이력서와 채용공고 요구조건을 비교하여 fitAnalysis를 작성하세요.
           11. resumeBased가 "false"이거나 resumeContent가 비어 있으면 fitAnalysis의 userCondition은 "이력서 없음" 또는 "확인 불가"로 작성하세요.

           [fitAnalysis 작성 규칙]

           1. status 값은 반드시 아래 enum 중 하나만 사용하세요.

              * SATISFIED
              * PARTIALLY_SATISFIED
              * NOT_SATISFIED

           2. category 값은 반드시 아래 값을 사용하세요.

              * education.category = EDUCATION
              * career.category = CAREER
              * certification.category = CERTIFICATION

           3. missingItems에는 사용자가 부족한 조건만 작성하세요.
              충족한 항목은 포함하지 마세요.

           4. missingItems 예시는 다음과 같습니다.

              * SQLD
              * AWS 자격증
              * 관련 경력 2년
              * React 실무 경험

           5. overallComments는 최대 3개까지만 작성하세요.

           6. overallComments의 각 항목은 한 문장의 자연스러운 한국어 문장으로 작성하세요.

           [추천 자격증 certificates 작성 규칙]

           1. certificates 배열은 최대 3개까지만 반환하세요.
           2. 추천 자격증은 채용공고의 필수사항, 우대사항, 주요 업무와 관련된 국가자격증 중심으로 추천하세요.
           3. 추천 우선순위는 다음과 같습니다.
              1순위: 채용공고에 직접 명시된 필수 자격증
              2순위: 채용공고에 직접 명시된 우대 자격증
              3순위: 채용공고의 주요 업무, 기술스택, 직무와 가장 관련성이 높은 국가자격증
           4. 채용공고에서 필수 또는 우대 자격증으로 명시된 자격증은 certificates 배열에 반드시 포함하세요.
           5. fitAnalysis.certification.missingItems에 포함된 자격증명은 certificates 배열에도 반드시 포함하세요.
           6. 채용공고에 자격증이 명시되지 않은 경우에도 직무 수행에 도움이 되는 국가자격증을 추천할 수 있습니다.
           7. 단, 채용공고가 기술 경험 중심이고 적절한 국가자격증 추천 근거가 부족한 경우 certificates는 빈 배열([])로 반환할 수 있습니다.
           8. resumeBased가 "true"이면 사용자가 이미 보유한 자격증보다 부족하거나 추가로 취득하면 좋은 자격증을 우선 추천하세요.
           9. certificationId는 항상 null로 반환하세요.
           10. reason은 왜 이 채용공고에 도움이 되는지 한 문장으로 작성하세요.
           11. relatedSkills는 채용공고에서 직접 언급된 기술을 우선 사용하세요.
           12. relatedSkills는 최대 5개까지만 작성하세요.
           13. difficulty는 반드시 아래 값 중 하나만 사용하세요.

               * 쉬움
               * 보통
               * 어려움

           [강의 추천 courses 작성 규칙]

           1. Gemini는 실제 LMS 강의를 추천하지 않습니다.
           2. courses는 항상 빈 배열([])을 반환하세요.
           3. 실제 강의 추천은 백엔드가 certificates와 relatedSkills를 기준으로 내부 LMS 강의 DB에서 조회하여 생성합니다.
           4. 실제 강의명, 강의 ID, 강의 설명을 절대 생성하지 마세요.

           [응답 형식 제한]

           1. 반드시 valid JSON만 반환하세요.
           2. Markdown code fence를 사용하지 마세요.
           3. JSON 외 설명, 주석, 안내 문구를 절대 포함하지 마세요.
           4. 응답은 하나의 JSON 객체만 반환하세요.
           5. JSON은 반드시 { 로 시작하고 } 로 종료하세요.
           6. JSON 종료 이후 어떠한 문자열도 출력하지 마세요.
           7. 모든 key 이름은 아래 JSON 구조와 동일해야 합니다.
           8. 값이 명확하지 않은 경우 빈 문자열, 빈 배열, "확인 불가"를 사용하세요.

           [응답 전 자체 검증]

           응답을 반환하기 전에 아래 사항을 반드시 확인하세요.

           1. JSON 형식이 올바른가?
           2. status가 허용된 enum만 사용되었는가?
           3. category가 허용된 enum만 사용되었는가?
           4. certificationId가 모두 null인가?
           5. certificates가 3개 이하인가?
           6. courses가 빈 배열([])인가?
           7. JSON 외 문장이 포함되지 않았는가?

           [반드시 반환할 JSON 구조]

           {
           "summary": {
           "jobRole": "",
           "requiredQualifications": [],
           "preferredQualifications": [],
           "experienceRequirement": "",
           "mainTaskSummary": ""
           },
           "fitAnalysis": {
           "education": {
           "category": "EDUCATION",
           "status": "SATISFIED",
           "requiredCondition": "",
           "userCondition": "",
           "comment": "",
           "missingItems": []
           },
           "career": {
           "category": "CAREER",
           "status": "PARTIALLY_SATISFIED",
           "requiredCondition": "",
           "userCondition": "",
           "comment": "",
           "missingItems": []
           },
           "certification": {
           "category": "CERTIFICATION",
           "status": "NOT_SATISFIED",
           "requiredCondition": "",
           "userCondition": "",
           "comment": "",
           "missingItems": []
           },
           "overallComments": []
           },
           "certificates": [
           {
           "certificationId": null,
           "name": "",
           "reason": "",
           "relatedSkills": [],
           "difficulty": ""
           }
           ],
           "courses": []
           }',
           4,
           true
       );
-- be1/V3.1.10__seed_additional_temporary_qualification_codes.sql
INSERT INTO qualification_codes (
    jm_cd,
    qualification_name,
    qualification_type_code,
    qualification_type_name,
    series_code,
    series_name,
    major_field_code,
    major_field_name,
    middle_field_code,
    middle_field_name,
    last_synced_at
)
SELECT
    '2290',
    '정보처리산업기사',
    'T',
    '국가기술자격',
    '01',
    '산업기사',
    '20',
    '정보통신',
    '201',
    '정보기술',
    NOW()
    WHERE NOT EXISTS (
    SELECT 1
    FROM qualification_codes
    WHERE jm_cd = '2290'
);

INSERT INTO qualification_codes (
    jm_cd,
    qualification_name,
    qualification_type_code,
    qualification_type_name,
    series_code,
    series_name,
    major_field_code,
    major_field_name,
    middle_field_code,
    middle_field_name,
    last_synced_at
)
SELECT
    '1325',
    '정보보안기사',
    'T',
    '국가기술자격',
    '01',
    '기사',
    '20',
    '정보통신',
    '201',
    '정보기술',
    NOW()
    WHERE NOT EXISTS (
    SELECT 1
    FROM qualification_codes
    WHERE jm_cd = '1325'
);

INSERT INTO qualification_codes (
    jm_cd,
    qualification_name,
    qualification_type_code,
    qualification_type_name,
    series_code,
    series_name,
    major_field_code,
    major_field_name,
    middle_field_code,
    middle_field_name,
    last_synced_at
)
SELECT
    '1324',
    '빅데이터분석기사',
    'T',
    '국가기술자격',
    '01',
    '기사',
    '20',
    '정보통신',
    '201',
    '정보기술',
    NOW()
    WHERE NOT EXISTS (
    SELECT 1
    FROM qualification_codes
    WHERE jm_cd = '1324'
);

INSERT INTO qualification_codes (
    jm_cd,
    qualification_name,
    qualification_type_code,
    qualification_type_name,
    series_code,
    series_name,
    major_field_code,
    major_field_name,
    middle_field_code,
    middle_field_name,
    last_synced_at
)
SELECT
    '2193',
    '사무자동화산업기사',
    'T',
    '국가기술자격',
    '01',
    '산업기사',
    '20',
    '정보통신',
    '201',
    '정보기술',
    NOW()
    WHERE NOT EXISTS (
    SELECT 1
    FROM qualification_codes
    WHERE jm_cd = '2193'
);
-- be5/V3.5.2__fix_instructor_profiles_category.sql
UPDATE instructor_profiles SET category_id = 1 WHERE instructor_profile_id = 1;
UPDATE instructor_profiles SET category_id = 7 WHERE instructor_profile_id = 2;
UPDATE instructor_profiles SET category_id = 3 WHERE instructor_profile_id = 3;
UPDATE instructor_profiles SET category_id = 8 WHERE instructor_profile_id = 4;
UPDATE instructor_profiles SET category_id = 2 WHERE instructor_profile_id = 5;
