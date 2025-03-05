db.createUser({
    user: "levelup",
    pwd: "levelup0210",
    roles: [
        { role: "userAdminAnyDatabase", db: "admin" },
        { role: "readWrite", db: "levelup" }
    ]
});