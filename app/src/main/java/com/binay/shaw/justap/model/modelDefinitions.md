# Android Project Model Documentation

Welcome to the documentation for the model classes used in our Android project. This documentation aims to provide a comprehensive overview of each model class, including their purpose, structure, and usage within the project.

## Table of Contents

1. [User](#user)
2. [LocalUser](#localuser)
3. [SocialAccount](#socialaccount)
4. [ScannedUser](#scanneduser)

---

## User

Represents a firebase user in the application.

### Properties

| Property            | Type      | Nullable | Description                                            |
|---------------------|-----------|----------|--------------------------------------------------------|
| `userID`            | `String`  | No       | Unique identifier for the user.                        |
| `name`              | `String`  | No       | Name of the user.                                      |
| `email`             | `String`  | No       | Email address of the user.                             |
| `bio`               | `String?` | Yes      | Biography of the user. Default value is an empty string.|
| `profilePictureURI` | `String?` | Yes      | URI for the profile picture of the user. Default value is an empty string. |
| `profileBannerURI`  | `String?` | Yes      | URI for the profile banner of the user. Default value is an empty string.  |


### Usage

```kotlin
data class User(
    val userID: String,
    val name: String,
    val email: String,
    val bio: String? = "",
    val profilePictureURI: String? = "",
    val profileBannerURI: String? = ""
)
```

### Example

```kotlin
val user = User(
    userID = "123456789",
    name = "John Doe",
    email = "john.doe@example.com",
    bio = "Software Engineer",
    profilePictureURI = "https://example.com/profile_picture.jpg",
    profileBannerURI = "https://example.com/profile_banner.jpg"
)
```

---


## LocalUser

Represents a user stored in the local database.

### Properties

| Property             | Type      | Nullable | Description                                            |
|----------------------|-----------|----------|--------------------------------------------------------|
| `userID`             | `String`  | No       | Unique identifier for the user.                        |
| `userName`           | `String`  | No       | Name of the user.                                      |
| `userEmail`          | `String`  | No       | Email address of the user.                             |
| `userBio`            | `String?` | Yes      | Biography of the user.                                 |
| `userProfilePicture` | `String?` | Yes      | URI for the profile picture of the user.              |
| `userBannerPicture`  | `String?` | Yes      | URI for the profile banner of the user.               |

### Usage

```kotlin
@Entity(tableName = "userDatabase")
data class LocalUser(
    @PrimaryKey(autoGenerate = false)
    val userID: String,
    val userName: String,
    val userEmail: String,
    val userBio: String?,
    val userProfilePicture: String?,
    val userBannerPicture: String?
) : Serializable
```


### Example 

```kotlin
val localUser = LocalUser(
    userID = "123456789",
    userName = "John Doe",
    userEmail = "john.doe@example.com",
    userBio = "Software Engineer",
    userProfilePicture = "https://example.com/profile_picture.jpg",
    userBannerPicture = "https://example.com/profile_banner.jpg"
)
```

---

# SocialAccount

Represents a social media or contact account.

## Properties

| Property        | Type      | Nullable | Description                                            |
|-----------------|-----------|----------|--------------------------------------------------------|
| `accountID`     | `Int`     | No       | Identifier for the account.                            |
| `accountName`   | `String`  | No       | Name or username associated with the account.          |
| `isVisible`     | `Boolean` | No       | Flag indicating whether the account is visible.        |
| `accountType`   | `String`  | No       | Type of the account (e.g., Facebook, Twitter, Email). |
| `isVerified`    | `Boolean` | No       | Flag indicating whether the account is verified.       |
| `creationDate`  | `Long`    | No       | Timestamp representing the creation date of the account.|
| `lastUpdated`   | `Long`    | No       | Timestamp representing the last update date of the account.|

## Usage

```kotlin
@Entity(tableName = "socialAccountsDatabase")
data class SocialAccount(
    @PrimaryKey(autoGenerate = false)
    var accountID: Int,
    val accountName: String,
    var isVisible: Boolean,
    var accountType: String,
    var isVerified: Boolean = false,
    var creationDate: Long = 0,
    var lastUpdated: Long = 0
) : Serializable
```


### Example

```kotlin
val socialAccount = SocialAccount(
    accountID = 123456,
    accountName = "example_username",
    isVisible = true,
    accountType = "Twitter",
    isVerified = true,
    creationDate = System.currentTimeMillis(), // Example timestamp for creation date
    lastUpdated = System.currentTimeMillis()   // Example timestamp for last update
)
```

---

## ScannedUser

Represents a scanned user entry in the local history.

### Properties

| Property     | Type      | Nullable | Description                                            |
|--------------|-----------|----------|--------------------------------------------------------|
| `scanID`     | `String`  | No       | Unique identifier for each scan entry.                 |
| `userID`     | `String`  | No       | User ID of the user who performed the scan.            |
| `username`   | `String`  | No       | Username associated with the scanned account.          |
| `updatedAt`  | `Long?`   | Yes      | Timestamp of when the scan entry was last updated.     |
| `profileImage`| `Bitmap?`| Yes      | Profile image associated with the scanned account.     |

### Usage

```kotlin
@Entity(tableName = "scannedUserDatabase")
data class ScannedUser(
    @PrimaryKey
    var scanID: String,
    var userID: String,
    var username: String,
    var updatedAt: Long?,
    @TypeConverters(BitmapConverter::class)
    var profileImage: Bitmap?
)
```

### Example usage:

```kotlin
val scannedAccount = ScannedUser(
    scanID = "unique_scan_id",
    userID = "user_id_123",
    username = "example_username",
    updatedAt = System.currentTimeMillis(),
    profileImage = profileImage
)
```
