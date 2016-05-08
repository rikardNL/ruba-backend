INSERT INTO "Tenants"
    ("id", "name")
    VALUES
        ('ruba-duba', 'Ruba'),
        ('netlight', 'Netlight');

INSERT INTO "Users"
    ("id", "email", "first_name", "last_name", "phone", "street_address", "city", "zip_code", "ssn", "role_description", "presentation")
    VALUES
        ('1', 'test1@test.com', 'Test', 'Testström',
        '+467000001', 'Testgatan 15', 'Staden', '123 45',
        '800808-8808', 'UX Designer', 'I am a happy social person who likes nice design'),
        ('2', 'test2@test.com', 'Test Tester', 'Tested Test',
        '+467000002', 'Testvägen 8A', 'Staden', '123 45',
        '800809-0003', 'App developer', 'I am a social guy who loves movies'),
        ('3', 'test3@test.com', 'Testsss', 'Testa Testers',
        '+467000003', 'Testgatan 7, apartment 1000', 'Byn', '345 67',
        '900909-9992', 'Full stack product guy', 'App developer'),
        ('4', 'test4@test.com', 'Test123', 'Andersson',
        '+467000004', 'Testlunden 16', 'Staden', '127 89',
        '840101-0101', 'Backend developer', 'App developer');

INSERT INTO "UsersTenants"
    ("user", "tenant")
    VALUES
        ('1', 'ruba-duba'),
        ('2', 'ruba-duba'),
        ('3', 'ruba-duba'),
        ('4', 'ruba-duba');

INSERT INTO "UserImages"
    ("user", "url")
    VALUES
        ('1', 'http://eighty4recruitment.com/cms/wp-content/uploads/2015/05/generic-person-trimmed.png'),
        ('1', 'https://upload.wikimedia.org/wikipedia/en/0/0b/Marge_Simpson.png'),
        ('2', 'https://pbs.twimg.com/profile_images/344513261574659983/f58ea74d16ca75ae8504c5770fbc5c44_400x400.jpeg'),
        ('2', 'https://upload.wikimedia.org/wikipedia/en/0/02/Homer_Simpson_2006.png'),
        ('3', 'http://www.astro-vision-avenir.com/images/IMAGES%20ANNONCES/Fotolia_5182070_XS%20-%20consult%20tarots.jpg'),
        ('3', 'https://upload.wikimedia.org/wikipedia/en/5/5a/Krustytheclown.png'),
        ('4', 'http://www.pakistanviews.org/media/k2/items/cache/55632af4b2a708a180d8c33754717382_Generic.jpg'),
        ('4', 'https://upload.wikimedia.org/wikipedia/en/8/86/Waylon_Smithers_1.png');

INSERT INTO "Teams"
    ("id", "tenant", "name", "image_url", "email", "description", "ambassador")
    VALUES
        ('1', 'ruba-duba', 'iOS developers',
        'https://upload.wikimedia.org/wikipedia/commons/0/09/Snow_white_1937_trailer_screenshot_%282%29.jpg',
        'ios@ruba.com', 'The team of great iOS developers', '2'),
        ('2', 'ruba-duba', 'UX designers',
        'https://upload.wikimedia.org/wikipedia/commons/6/6f/Steve_Jobs_with_Wendell_Brown_at_the_launch_of_Brown%27s_Hippo-C_software_for_Macintosh%2C_January_1984.jpg',
        'ux@ruba.com', 'The team of awesome UX designers', '1'),
        ('3', 'ruba-duba', 'Android developers',
        'http://www.android.com/media/android_vector.jpg',
        'android@ruba.com', 'Team of droids', '2'),
        ('4', 'ruba-duba', 'Backend developers',
        'http://rack.0.mshcdn.com/media/ZgkyMDEzLzAzLzI3L2Q4L21haW5mcmFtZS4yMWRmNC5qcGcKcAl0aHVtYgkxMjAweDYyNyMKZQlqcGc/ed4044a2/759/mainframe.jpg',
        'be@ruba.com', 'The team of amazing backend developers', '4');

INSERT INTO "UsersTeams"
    ("user", "team")
    VALUES
        ('1', '2'),
        ('2', '1'),
        ('2', '2'),
        ('2', '3'),
        ('3', '1'),
        ('3', '2'),
        ('3', '4'),
        ('4', '3'),
        ('4', '4');
