package de.iani.cubesideutils.bukkit.items;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public enum CustomHeads {
    QUARTZ_ARROW_UP("3554e03b-982d-44f1-8be4-71785ba822f8", "Quartz Arrow Up", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFkNmM4MWY4OTlhNzg1ZWNmMjZiZTFkYzQ4ZWFlMmJjZmU3NzdhODYyMzkwZjU3ODVlOTViZDgzYmQxNGQifX19"),
    QUARTZ_ARROW_DOWN("8f54d1c4-c599-4c54-8993-2cb371649c33", "Quartz Arrow Down", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODgyZmFmOWE1ODRjNGQ2NzZkNzMwYjIzZjg5NDJiYjk5N2ZhM2RhZDQ2ZDRmNjVlMjg4YzM5ZWI0NzFjZTcifX19"),
    QUARTZ_ARROW_LEFT("4d35f021-81b6-44ee-a711-8d8462174124", "Quartz Arrow Left", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzNlOTE5MTlkYjBhY2VmZGMyNzJkNjdmZDg3YjRiZTg4ZGM0NGE5NTg5NTg4MjQ0NzRlMjFlMDZkNTNlNiJ9fX0="),
    QUARTZ_ARROW_RIGHT("1f961930-4e97-47b7-a5a1-2cc5150f3764", "Quartz Arrow Right", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0="),
    QUARTZ_BLOCK_BLANK("3fe6ca71-3da7-4708-a837-0a4211b73df7", "Quartz Blank", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzcyMzcwNGE5ZDU5MTBiOWNkNTA1ZGM5OWM3NzliZjUwMzc5Y2I4NDc0NWNjNzE5ZTlmNzg0ZGQ4YyJ9fX0="),
    QUARTZ_PLUS("bafea105-1289-46d4-a3a6-822bc1cd4bd7", "Quartz Plus", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdhMGZjNmRjZjczOWMxMWZlY2U0M2NkZDE4NGRlYTc5MWNmNzU3YmY3YmQ5MTUzNmZkYmM5NmZhNDdhY2ZiIn19fQ=="),
    QUARTZ_MINUS("37fa8deb-3207-4f29-9823-10316eb12ec7", "Quartz Minus", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ2YjEyOTNkYjcyOWQwMTBmNTM0Y2UxMzYxYmJjNTVhZTVhOGM4ZjgzYTE5NDdhZmU3YTg2NzMyZWZjMiJ9fX0="),
    QUARTZ_CHECKMARK("ac8bc90d-88dc-4ef3-8e4f-14c60cf41709", "Quartz Checkmark", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdiNjJkMjc1ZDg3YzA5Y2UxMGFjYmNjZjM0YzRiYTBiNWYxMzVkNjQzZGM1MzdkYTFmMWRmMzU1YTIyNWU4MiJ9fX0="),
    QUARTZ_QUESTION_MARK("db30a037-bf94-421a-a2bd-8fe7f5d76dd9", "Quartz Question Mark", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmMyNzEwNTI3MTllZjY0MDc5ZWU4YzE0OTg5NTEyMzhhNzRkYWM0YzI3Yjk1NjQwZGI2ZmJkZGMyZDZiNWI2ZSJ9fX0="),
    QUARTZ_EXCLAMATION_MARK("f56bc536-4a0a-4c8f-900c-226bf1d9a001", "Quartz Exclamation Mark", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I4N2QyMjUyY2FjMWFhMTVkZjMyNTk5OGI4ZWM4MmVmOTEwOWI2YzU2NzYxMGFmYWMwZWNkYTUxM2Y2MSJ9fX0="),
    QUARTZ_X("51bdc491-3e72-4c1b-af6d-a0db5e4a2bc7", "Quartz X", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkxZDZlZGE4M2VkMmMyNGRjZGNjYjFlMzNkZjM2OTRlZWUzOTdhNTcwMTIyNTViZmM1NmEzYzI0NGJjYzQ3NCJ9fX0="),

    QUARTZ_0("2a9e0930-526b-4e86-971c-25415328ff7a", "Quartz 0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWY4ODZkOWM0MGVmN2Y1MGMyMzg4MjQ3OTJjNDFmYmZiNTRmNjY1ZjE1OWJmMWJjYjBiMjdiM2VhZDM3M2IifX19"),
    QUARTZ_1("f6db7588-352e-4074-86a8-4f58e2a8a74e", "Quartz 1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBhMTllMjNkMjFmMmRiMDYzY2M1NWU5OWFlODc0ZGM4YjIzYmU3NzliZTM0ZTUyZTdjN2I5YTI1In19fQ=="),
    QUARTZ_2("517901d5-c5bc-4c07-a3f0-a0537d190f14", "Quartz 2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M1OTZhNDFkYWVhNTFiZTJlOWZlYzdkZTJkODkwNjhlMmZhNjFjOWQ1N2E4YmRkZTQ0YjU1OTM3YjYwMzcifX19"),
    QUARTZ_3("b83b4f82-fad3-426f-a91d-e9325a5b39dd", "Quartz 3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg1ZDRmZGE1NmJmZWI4NTEyNDQ2MGZmNzJiMjUxZGNhOGQxZGViNjU3ODA3MGQ2MTJiMmQzYWRiZjVhOCJ9fX0="),
    QUARTZ_4("e3e876f9-2bc2-42a8-b480-4eaf0a00e803", "Quartz 4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg1MmEyNWZlNjljYTg2ZmI5ODJmYjNjYzdhYzk3OTNmNzM1NmI1MGI5MmNiMGUxOTNkNmI0NjMyYTliZDYyOSJ9fX0="),
    QUARTZ_5("c535d7f1-931e-4fab-a4f1-1f67e6ed868d", "Quartz 5", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlZTdkOTU0ZWIxNGE1Y2NkMzQ2MjY2MjMxYmY5YTY3MTY1MjdiNTliYmNkNzk1NmNlZjA0YTlkOWIifX19"),
    QUARTZ_6("919f6697-ac07-41a2-90b4-1fc14c3afe48", "Quartz 6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY4MmEzYWU5NDgzNzRlMDM3ZTNkN2RkNjg3ZDU5ZDE4NWRkMmNjOGZjMDlkZmViNDJmOThmOGQyNTllNWMzIn19fQ=="),
    QUARTZ_7("8b614d52-9a8c-4780-9f31-2021fc06aa4f", "Quartz 7", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVhMzBjMjRjNjBiM2JjMWFmNjU4ZWY2NjFiNzcxYzQ4ZDViOWM5ZTI4MTg4Y2Y5ZGU5ZjgzMjQyMmU1MTAifX19"),
    QUARTZ_8("ef959987-ff3f-44f5-8a0a-07ac776832fa", "Quartz 8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjZhYmFmZDAyM2YyMzBlNDQ4NWFhZjI2ZTE5MzY4ZjU5ODBkNGYxNGE1OWZjYzZkMTFhNDQ2Njk5NDg5MiJ9fX0="),
    QUARTZ_9("fcc24a38-ef01-435a-a55d-e32451353e44", "Quartz 9", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQ3OTEwZTEwMzM0Zjg5MGE2MjU0ODNhYzBjODI0YjVlNGExYTRiMTVhOTU2MzI3YTNlM2FlNDU4ZDllYTQifX19"),

    OAK_WOOD_0("2298cff2-c1a5-4278-a277-8d8661afe1c6", "Oak Wood 0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMGViZTdlNTIxNTE2OWE2OTlhY2M2Y2VmYTdiNzNmZGIxMDhkYjg3YmI2ZGFlMjg0OWZiZTI0NzE0YjI3In19fQ=="),
    OAK_WOOD_1("00684a88-5cc8-4713-9e91-7b1906e67580", "Oak Wood 1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFiYzJiY2ZiMmJkMzc1OWU2YjFlODZmYzdhNzk1ODVlMTEyN2RkMzU3ZmMyMDI4OTNmOWRlMjQxYmM5ZTUzMCJ9fX0="),
    OAK_WOOD_2("f7218833-aceb-4d3e-a1bc-a334be09c375", "Oak Wood 2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNkOWVlZWU4ODM0Njg4ODFkODM4NDhhNDZiZjMwMTI0ODVjMjNmNzU3NTNiOGZiZTg0ODczNDE0MTk4NDcifX19"),
    OAK_WOOD_3("870c6ce6-78b5-4e09-8745-bd96d616e516", "Oak Wood 3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQ0ZWFlMTM5MzM4NjBhNmRmNWU4ZTk1NTY5M2I5NWE4YzNiMTVjMzZiOGI1ODc1MzJhYzA5OTZiYzM3ZTUifX19"),
    OAK_WOOD_4("d531b607-3d92-4760-b19f-b64d51da0fa5", "Oak Wood 4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJlNzhmYjIyNDI0MjMyZGMyN2I4MWZiY2I0N2ZkMjRjMWFjZjc2MDk4NzUzZjJkOWMyODU5ODI4N2RiNSJ9fX0="),
    OAK_WOOD_5("4aaa0af9-ffde-4f5a-ad06-112dffbade0c", "Oak Wood 5", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ1N2UzYmM4OGE2NTczMGUzMWExNGUzZjQxZTAzOGE1ZWNmMDg5MWE2YzI0MzY0M2I4ZTU0NzZhZTIifX19"),
    OAK_WOOD_6("58a05887-3473-4c87-8506-2acf877d7ff1", "Oak Wood 6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzM0YjM2ZGU3ZDY3OWI4YmJjNzI1NDk5YWRhZWYyNGRjNTE4ZjVhZTIzZTcxNjk4MWUxZGNjNmIyNzIwYWIifX19"),
    OAK_WOOD_7("23378bd2-28e5-4d7e-8d39-621b732e1f49", "Oak Wood 7", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRiNmViMjVkMWZhYWJlMzBjZjQ0NGRjNjMzYjU4MzI0NzVlMzgwOTZiN2UyNDAyYTNlYzQ3NmRkN2I5In19fQ=="),
    OAK_WOOD_8("19c144be-a435-42a4-9503-83bcd8a7fa70", "Oak Wood 8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTkxOTQ5NzNhM2YxN2JkYTk5NzhlZDYyNzMzODM5OTcyMjI3NzRiNDU0Mzg2YzgzMTljMDRmMWY0Zjc0YzJiNSJ9fX0="),
    OAK_WOOD_9("c7cad554-93b2-4176-a4ba-8de42aa9c9f2", "Oak Wood 9", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY3Y2FmNzU5MWIzOGUxMjVhODAxN2Q1OGNmYzY0MzNiZmFmODRjZDQ5OWQ3OTRmNDFkMTBiZmYyZTViODQwIn19fQ=="),

    OAK_WOOD_A("e8e10bc5-b94e-4378-a54c-ac71a662fec9", "Oak Wood A", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTY3ZDgxM2FlN2ZmZTViZTk1MWE0ZjQxZjJhYTYxOWE1ZTM4OTRlODVlYTVkNDk4NmY4NDk0OWM2M2Q3NjcyZSJ9fX0="),
    OAK_WOOD_R("9af104ca-dfbb-4616-9101-d10016c2b11a", "Oak Wood R", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVjZWQ5OTMxYWNlMjNhZmMzNTEzNzEzNzliZjA1YzYzNWFkMTg2OTQzYmMxMzY0NzRlNGU1MTU2YzRjMzcifX19"),
    OAK_WOOD_S("826bfe2f-387c-44d7-9cd3-fc3046a6204d", "Oak Wood S", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2U0MWM2MDU3MmM1MzNlOTNjYTQyMTIyODkyOWU1NGQ2Yzg1NjUyOTQ1OTI0OWMyNWMzMmJhMzNhMWIxNTE3In19fQ=="),
    OAK_WOOD_T("08c08ff3-95f3-44cc-8da3-d0e1331cf469", "Oak Wood T", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTU2MmU4YzFkNjZiMjFlNDU5YmU5YTI0ZTVjMDI3YTM0ZDI2OWJkY2U0ZmJlZTJmNzY3OGQyZDNlZTQ3MTgifX19"),

    RAINBOW_A("0db25a65-ac69-4057-94ca-09b73a0b6bff", "Rainbow A", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTUxN2I0ODI5YjgzMTkyYmQ3MjcxMTI3N2E4ZWZjNDE5NjcxMWU0MTgwYzIyYjNlMmI4MTY2YmVhMWE5ZGUxOSJ9fX0="),
    RAINBOW_R("5ee68035-e68e-4591-91b1-c30c25710d66", "Rainbow R", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTU4MjdmNDVhYWU2NTY4MWJiMjdlM2UwNDY1YWY2MjI4ZWQ2MjkyYmI2M2IwYTc3NjQ1OTYyMjQ3MjdmOGQ4MSJ9fX0="),
    RAINBOW_S("830419ec-9906-45c5-8c59-29587cdd4a39", "Rainbow S", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGNkN2QxNGM2ZGI4NDFlNTg2NDUxMWQxNmJhNzY3MGIzZDIwMzgxNDI0NjY5ODFmZWIwNWFmYzZlNWVkYzZjYiJ9fX0="),
    RAINBOW_T("4268d321-bd34-4148-a6c0-c948a6450da0", "Rainbow T", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk0YWMzNmQ5YTZmYmZmMWM1NTg5NDEzODFlNGRjZjU5NWRmODI1OTEzZjZjMzgzZmZhYTcxYjc1NmE4NzVkMyJ9fX0="),
    RAINBOW_X("4d37c12c-eb19-4499-8c62-33d84c4d9761", "Rainbow X", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTVkNWM3NWY2Njc1ZWRjMjkyZWEzNzg0NjA3Nzk3MGQyMjZmYmQ1MjRlN2ZkNjgwOGYzYTQ3ODFhNTQ5YjA4YyJ9fX0="),

    RAINBOW_ARROW_UP("daf047f5-cf80-4fdf-bbc8-d7b676c97fcf", "Rainbow Arrow Up", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWNkYjhmNDM2NTZjMDZjNGU4NjgzZTJlNjM0MWI0NDc5ZjE1N2Y0ODA4MmZlYTRhZmYwOWIzN2NhM2M2OTk1YiJ9fX0="),
    RAINBOW_ARROW_DOWN("2b529a28-1db9-4450-a3eb-6c9897417bbb", "Rainbow Arrow Down", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFlMWU3MzBjNzcyNzljOGUyZTE1ZDhiMjcxYTExN2U1ZTJjYTkzZDI1YzhiZTNhMDBjYzkyYTAwY2MwYmI4NSJ9fX0="),
    RAINBOW_ARROW_LEFT("375056b3-1bbb-47c3-8f49-1284b46176f7", "Rainbow Arrow Left", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFjOTZhNWMzZDEzYzMxOTkxODNlMWJjN2YwODZmNTRjYTJhNjUyNzEyNjMwM2FjOGUyNWQ2M2UxNmI2NGNjZiJ9fX0="),
    RAINBOW_ARROW_RIGHT("aaea5de0-a21a-4d07-bdb3-060b47085107", "Rainbow Arrow Right", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzMzYWU4ZGU3ZWQwNzllMzhkMmM4MmRkNDJiNzRjZmNiZDk0YjM0ODAzNDhkYmI1ZWNkOTNkYThiODEwMTVlMyJ9fX0="),
    RAINBOW_ARROW_BACKWARD_II("3b0bfc9d-474f-45c2-88e5-faee505e0885", "Rainbow Backward II", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWZjZTQ3NWE0Mzg0ZTA5ZTMyY2Q4ZTkxZDA5Yzc5NDdhZGY3ODI3MzA3ZmRhZGRiYWYyOTk4NTE0OTQ4ZmI2ZSJ9fX0="),
    RAINBOW_ARROW_FORWARD_II("a090e767-c207-4f3b-a207-9671389c120f", "Rainbow Forward II", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzkyMGYwZDRiMmUwMTBlODAxNTNhNjhiNDU1MDFkMDU0YzQzYmIyNDhmNDRiYjgzNzM2NDBlMzIzNTY3OWFjMyJ9fX0="),
    RAINBOW_BLANK("fb0cf49a-a3fd-4fd9-be63-0833bab9566e", "Rainbow Blank", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzJiNjczMjE5OWZhZjFiMjQ0NzcyNWU1NjgyOTA5MGZiY2ViNmMyYjUxNDk1Mzg2MmZmMDNjMTZiNTNmMzU5OSJ9fX0="),

    REDSTONE_BLOCK_0("a4ce51b7-af2c-4217-bd56-da49838ca16f", "Redstone Block 0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDRjNmIxOWEwMTQ0ZjYzZmQyNmE2OTBmMjAxNjQwYjRiMmYxNTFlZWY3MmNjNDliZmUwNjEyMDQ0Y2VhNTZlNyJ9fX0="),
    REDSTONE_BLOCK_1("c024d5ba-3692-41c4-b901-04e7d61a1c99", "Redstone Block 1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM5Yzg0NmY2NWQ1ZjI3MmE4MzlmZDljMmFlYjExYmRjOGUzZjgyMjlmYmUzNTgzNDg2ZTc4ZjRjMjNjOGI1YiJ9fX0="),
    REDSTONE_BLOCK_2("4ae31ef1-df6f-4708-92fe-606308cd660e", "Redstone Block 2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQxNWM0ZDBjN2I4MTQxNTAxOTQ5ZjczY2UwYzc4YjJiMWU5OTAyNTUzNzFhN2ZjNzE5OTk2MGM5YjAzN2Q1MSJ9fX0="),
    REDSTONE_BLOCK_3("52bde801-c1d6-44c0-bac3-5cfb6decd49a", "Redstone Block 3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY4ZDNjOGNiMDk4M2E0ZjU2Y2MyNmE3MWZmY2VkYmQ3YmVjYzUyMTI5MWM3ODM2MWZmMWU5OWRmNDE0NGNiYyJ9fX0="),
    REDSTONE_BLOCK_4("d01d149b-4c64-4669-83fb-998eda89fdb8", "Redstone Block 4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjEyNzgxMjE2NmUxNDE4NmRlY2YxNzUxOTYwM2IzNTU2OTk0OTlhNTQ1Mzk3Zjg5MzE3OTRmYWQ2ZTllZmQ5MiJ9fX0="),
    REDSTONE_BLOCK_5("a0089496-62c7-4781-a525-d40d1258b82f", "Redstone Block 5", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmUxMDA4NTkyZTNhZDI0ZDY1ZGZhNGZmNWEzYzgwMGQ3OGEzZGIxMzRjYmQ4ZTllYzNjYmFjMWVhODM5MWI5ZCJ9fX0="),
    REDSTONE_BLOCK_6("1ecbb4bb-23ab-40e3-9b2e-b4a7bc628eef", "Redstone Block 6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMwOThmM2E5OTRjMWNkNjhlMGU4NjJhMDA2ODg4NjZmMmU2NzM0ODFjZDM0NDdjODVkOTgwMWJjMDMxN2I1ZiJ9fX0="),
    REDSTONE_BLOCK_7("b767bec2-4b9e-4755-95c2-4f04297a2d10", "Redstone Block 7", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODEwMmE4ZWIwZmZkZmU1OTgyMDczZGJjNDFiNzViYzIyZTU3N2UzYjFhZDAwYmIxNDg2OGNlZTM4NGJlYzdiIn19fQ=="),
    REDSTONE_BLOCK_8("88dd88fb-ede5-43b0-ba4f-960fa186ee13", "Redstone Block 8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNhNmQ5ZWNhNjg2Mjg1MThlNmI5OTA1NGJjMGExZjdmY2M3OWUyYzk2OWYzZWI4ZjllZjAzNDE2NWUwM2JiNSJ9fX0="),
    REDSTONE_BLOCK_9("bea45512-537c-45b2-8394-4ff3c27ade5a", "Redstone Block 9", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjUyMjc0NmYyZDExMTEzZGIyYzdkZTVkYzczNTI3NjE0MmRjNGJhZGU3YmVkNDczNDQyYTk1NGExYzQyMjc5ZiJ9fX0="),

    TNT("f0284b28-c50b-4494-b0d7-124c6e8a24ce", "TNT", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTBlNGM2NzBjOWIyYzE5YTQ4YTkyMzExYThkN2Y4MmEzOTY1YmMyOTdhMjIwYzg5ZTE2NjgyNTQxN2U4In19fQ=="),
    ;

    private ItemStack head;

    private CustomHeads(String ownerUUIDString, String ownerName, String texturesProperty) {
        head = createHead(UUID.fromString(ownerUUIDString), ownerName, texturesProperty);
    }

    public ItemStack getHead() {
        return new ItemStack(head);
    }

    @Deprecated
    public ItemStack getHead(String displayName) {
        return getHead(displayName, (String[]) null);
    }

    @Deprecated
    public ItemStack getHead(String displayName, String... lore) {
        ItemStack stack = getHead();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null && lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getHead(Component displayName) {
        return getHead(displayName, (Component[]) null);
    }

    public ItemStack getHead(Component displayName, Component... lore) {
        return getHead(displayName, lore != null && lore.length > 0 ? Arrays.asList(lore) : null);
    }

    public ItemStack getHead(Component displayName, List<Component> lore) {
        ItemStack stack = getHead();
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(displayName);
        if (lore != null && lore.size() > 0) {
            meta.lore(lore);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public static String cleanProfileName(String in) {
        if (in == null) {
            return null;
        }
        StringBuilder sb = null;
        int length = in.length();
        for (int i = 0; i < length; i++) {
            char c = in.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_') {
                if (sb != null) {
                    sb.append(c);
                    if (sb.length() == 16) {
                        return sb.toString();
                    }
                } else {
                    if (i >= 16) {
                        return in.substring(0, 16);
                    }
                }
            } else {
                if (sb == null) {
                    if (i >= 16) {
                        return in.substring(0, 16);
                    }
                    sb = new StringBuilder(in.substring(0, i));
                }
            }
        }
        return sb == null ? (in.length() <= 16 ? in : in.substring(0, 16)) : sb.toString();
    }

    public static ItemStack createHead(UUID ownerUUID, String ownerName, String texturesProperty) {
        ownerName = cleanProfileName(ownerName);
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(ownerUUID, ownerName);
        profile.setProperty(new ProfileProperty("textures", texturesProperty));
        meta.setPlayerProfile(profile);
        stack.setItemMeta(meta);
        return stack;
    }
}
