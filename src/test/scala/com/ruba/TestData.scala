package com.ruba

import com.ruba.api.{Team, Tenant, User}

trait TestData {
  val testTenants = Seq(
    Tenant(
      id = "ruba-duba",
      name = "Ruba"
    ),
    Tenant(
      id = "netlight",
      name = "Netlight Consulting"
    )
  )
  val tenant1 = testTenants.head
  val tenant2 = testTenants.last

  val testTeams = Seq(
    Team(
      id = Some("1"),
      name = "iOS developers",
      imageUrl = Some("https://upload.wikimedia.org/wikipedia/commons/0/09/Snow_white_1937_trailer_screenshot_%282%29.jpg"),
      email = Some("ios@ruba.com"),
      description = Some("The team of great iOS developers"),
      ambassadorId = Some("2"),
      memberIds = Seq("2", "3")
    ),
    Team(
      id = Some("2"),
      name = "UX designers",
      imageUrl = Some("https://upload.wikimedia.org/wikipedia/commons/6/6f/Steve_Jobs_with_Wendell_Brown_at_the_launch_of_Brown%27s_Hippo-C_software_for_Macintosh%2C_January_1984.jpg"),
      email = Some("ux@ruba.com"),
      description = Some("The team of awesome UX designers"),
      ambassadorId = Some("1"),
      memberIds = Seq("1", "2", "3")
    ),
    Team(
      id = Some("3"),
      name = "Android developers",
      imageUrl = Some("http://www.android.com/media/android_vector.jpg"),
      email = Some("android@ruba.com"),
      description = None,
      ambassadorId = Some("2"),
      memberIds = Seq("2", "4")
    ),
    Team(
      id = Some("4"),
      name = "Backend developers",
      imageUrl = Some("http://rack.0.mshcdn.com/media/ZgkyMDEzLzAzLzI3L2Q4L21haW5mcmFtZS4yMWRmNC5qcGcKcAl0aHVtYgkxMjAweDYyNyMKZQlqcGc/ed4044a2/759/mainframe.jpg"),
      email = Some("be@ruba.com"),
      description = Some("The team of amazing backend developers"),
      ambassadorId = Some("4"),
      memberIds = Seq("3", "4")
    )
  )

  val team1 = testTeams.head
  val team2 = testTeams.drop(1).head
  val team3 = testTeams.drop(2).head
  val team4 = testTeams.drop(3).head

  val team1Clean = team1.copy(ambassadorId = None, memberIds = Seq.empty)
  val team2Clean = team2.copy(ambassadorId = None, memberIds = Seq.empty)
  val team3Clean = team3.copy(ambassadorId = None, memberIds = Seq.empty)
  val team4Clean = team4.copy(ambassadorId = None, memberIds = Seq.empty)

  val testUsers = Seq(
    User(
      id = Some("1"),
      email = "test.tester@test.com",
      firstName = Some("Test"),
      lastName = Some("Testersson"),
      phone = Some("+46700000001"),
      streetAddress = Some("Testgatan 15 lgh 123"),
      city = Some("Lillastaden"),
      zipCode = Some("123 45"),
      socialSecurityNumber = Some("890808-1122"),
      roleDescription = Some("UX Designer"),
      presentation = Some("I am a happy social person who likes nice design"),
      imageUrls = Seq(
        "https://upload.wikimedia.org/wikipedia/en/0/0b/Marge_Simpson.png"
      ),
      backgroundImage = None,
      teamIds = Seq("2"),
      ambassadorForTeams = Seq("2")
    ),
    User(
      id = Some("2"),
      email = "test2@test.com",
      firstName = Some("Test Test"),
      lastName = Some("Test-Testtest Svensson"),
      phone = Some("+467000003"),
      streetAddress = Some("Gatan 8A, apartment 1"),
      city = Some("Staden"),
      zipCode = Some("123 45"),
      socialSecurityNumber = Some("830101-0101"),
      roleDescription = Some("App developer"),
      presentation = Some("I am a social guy who loves movies"),
      imageUrls = Seq(
        "https://upload.wikimedia.org/wikipedia/en/0/02/Homer_Simpson_2006.png"
      ),
      backgroundImage = None,
      teamIds = Seq("1", "2", "3"),
      ambassadorForTeams = Seq("1", "3")
    ),
    User(
      id = Some("3"),
      email = "test3@test.com",
      firstName = Some("Wow"),
      lastName = Some("Test Wow"),
      phone = Some("+467000005"),
      streetAddress = Some("Gatgatan 7"),
      city = Some("Byn"),
      zipCode = Some("123 32"),
      socialSecurityNumber = Some("841111-1111"),
      roleDescription = Some("Full stack product guy"),
      presentation = Some("App developer"),
      imageUrls = Seq(
        "https://upload.wikimedia.org/wikipedia/en/5/5a/Krustytheclown.png"
      ),
      backgroundImage = None,
      teamIds = Seq("1", "2", "3"),
      ambassadorForTeams = Seq()
    ),
    User(
      id = Some("4"),
      email = "test4.andersson@test.com",
      firstName = Some("test4"),
      lastName = Some("Andersson"),
      phone = Some("+467000004"),
      streetAddress = Some("Vägen 16"),
      city = Some("Staden"),
      zipCode = Some("123 35"),
      socialSecurityNumber = Some("840101-1111"),
      roleDescription = Some("Backend developer"),
      presentation = Some("App developer"),
      imageUrls = Seq(
        "https://upload.wikimedia.org/wikipedia/en/8/86/Waylon_Smithers_1.png"
      ),
      backgroundImage = None,
      teamIds = Seq.empty,
      ambassadorForTeams = Seq.empty
    )
  )

  val user1 = testUsers.head
  val user2 = testUsers.drop(1).head
  val user3 = testUsers.drop(2).head
  val user4 = testUsers.drop(3).head

  val user1Clean = user1.copy(teamIds = Seq.empty, ambassadorForTeams = Seq.empty)
  val user2Clean = user2.copy(teamIds = Seq.empty, ambassadorForTeams = Seq.empty)
  val user3Clean = user3.copy(teamIds = Seq.empty, ambassadorForTeams = Seq.empty)
  val user4Clean = user4.copy(teamIds = Seq.empty, ambassadorForTeams = Seq.empty)
}
