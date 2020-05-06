create table Users (
    firstName char(100),
    lastName char(100),
    email char(100),
    credit integer,
    password char(100),
    primary key (email)
);

create table Restaurants (
    id char(100),
    name char(100),
    logoUrl char(255),
    x float,
    y float,
    primary key (id)
);

create table Foods (
    id integer NOT NULL AUTO_INCREMENT,
    name char(100),
    description text,
    popularity float,
    imageUrl char(255),
    price integer,
    count integer,
    primary key (id)
);

create table PartyFoods (
    id integer NOT NULL AUTO_INCREMENT,
    foodId integer,
    newPrice integer,
    count integer,
    valid char(1),
    primary key (id),
    foreign key (foodId) references Foods(id) on delete cascade
);

create table Menu (
    restaurantId char(100),
    foodId integer,
    primary key (restaurantId, foodId),
    foreign key (restaurantId) references Restaurants(id) on delete cascade,
    foreign key (foodId) references Foods(id) on delete cascade
);

create table PartyMenu (
    restaurantId char(100),
    partyFoodId integer,
    primary key (restaurantId, partyFoodId),
    foreign key (restaurantId) references Restaurants(id) on delete cascade,
    foreign key (partyFoodId) references PartyFoods(id) on delete cascade
);

create table Orders (
    id integer NOT NULL AUTO_INCREMENT,
    username char(50),
    restaurantId char(100),
    status ENUM ('searching', 'delivering', 'done', 'notFinalized'),
    registerTime DATETIME,
    primary key (id),
    foreign key (username) references Users(email) on delete cascade,
    foreign key (restaurantId) references Restaurants(id) on delete cascade
);

create table OrderRows (
    id integer NOT NULL AUTO_INCREMENT,
    orderId integer,
    foodId integer NULL,
    partyFoodId integer NULL,
    count integer,
    foodType ENUM ('normal', 'party'),
    primary key (id),
    foreign key (orderId) references Orders(id) on delete cascade,
    foreign key (foodId) references Foods(id) on delete cascade,
    foreign key (partyFoodId) references PartyFoods(id) on delete cascade
);
