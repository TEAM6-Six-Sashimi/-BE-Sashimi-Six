-- instructor01(김강사), instructor02(이강사) main_careers 더미 데이터 추가
-- V1 INSERT 당시 main_careers 컬럼이 없었기 때문에 NULL 상태로 남겨진 것을 보완

UPDATE instructor_profiles
SET main_careers = JSON_ARRAY(
    '(주)스마트이노베이션 백엔드 개발자 (2015.03 ~ 2021.06)',
    '프리랜서 IT 강의 및 멘토링 (2021.07 ~ 2023.12)',
    '정보처리기사 취득 (2014)',
    'SQLD 취득 (2016)'
)
WHERE user_id = (SELECT user_id FROM users WHERE login_id = 'instructor01');

UPDATE instructor_profiles
SET main_careers = JSON_ARRAY(
    '(주)데이터허브 데이터 분석가 (2016.02 ~ 2022.08)',
    '빅데이터 컨설팅 프리랜서 (2022.09 ~ 2024.06)',
    'ADsP 취득 (2015)',
    '빅데이터분석기사 취득 (2021)'
)
WHERE user_id = (SELECT user_id FROM users WHERE login_id = 'instructor02');
