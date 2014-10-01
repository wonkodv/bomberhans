package org.hanstool.bomberhans.shared;

import java.util.Random;

public class Const
{
	public static boolean	logging	= false;
	
	public static class CellTypes
	{
		public static final byte		UNKNOWN			= 0;
		public static final byte		CLEAR			= 1;
		public static final byte		WALL			= 2;
		public static final byte		WOOD			= 3;
		public static final byte		BURNING_WOOD	= 4;
		public static final byte		BOMB			= 5;
		public static final byte		FIRE_CENTER		= 6;
		public static final byte		FIRE_HORZ		= 7;
		public static final byte		FIRE_VERT		= 8;
		public static final byte		FIRE_END_N		= 9;
		public static final byte		FIRE_END_E		= 10;
		public static final byte		FIRE_END_S		= 11;
		public static final byte		FIRE_END_W		= 12;
		public static final byte		HANS_DEAD		= 13;
		public static final byte		HANS_BURNING	= 14;
		public static final byte		HANS_GORE		= 15;
		public static final byte		PU_BOMB			= 16;
		public static final byte		PU_POWER		= 17;
		public static final byte		PU_SPEED		= 18;
		public static final byte		SP_PORT			= 19;
		public static final byte		SP_SCHINKEN		= 20;
		public static final byte		START_POINT		= 21;
		public static final String[]	NAMES			= {
																"invalid",
																"cell_clear",
																"cell_wall",
																"cell_wood",
																"cell_wood_burning",
																"cell_bomb",
																"cell_fire_center",
																"cell_fire_horz",
																"cell_fire_vert",
																"cell_fire_end_n",
																"cell_fire_end_e",
																"cell_fire_end_s",
																"cell_fire_end_w",
																"cell_hans_dead",
																"cell_hans_burning",
																"cell_hans_gore",
																"cell_pu_bomb",
																"cell_pu_power",
																"cell_pu_speed",
																"cell_sp_port",
																"cell_sp_schinken",
																"cell_startpoint" };
	}
	
	public static class GameConsts
	{
		public static final int		MAX_PLAYERS				= 8;
		public static final int		FIRE_TTL				= 1250;
		public static final int		BOMB_WALKOVER			= 60;
		public static final int		BURNING_WOOD_TTL		= 1800;
		public static final int		BOMB_TTL_AFTER_BURNING	= 400;
		public static final int		BURNING_HANS_TTL		= 4250;
		public static final int		PLAYER_BASE_POWER		= 2;
		public static final float	PLAYER_BASE_SPEED		= 1.3F;
		public static final float	PU_GAIN					= 0.5F;
		public static final float	PU_RDUCE				= 0.8F;
		public static final int		WTS_WALL				= 1;
		public static final int		WTS_POWER				= 101;
		public static final int		WTS_SPEED				= 201;
		public static final int		WTS_BOMBS				= 301;
		public static final int		WTS_PORT				= 401;
		public static final int		WTS_SCHINKEN			= 416;
		public static final int		ANIMATION_TIME			= 200;
		public static final int		SPECIAL_EXPLODE_POWER	= 1;
		public static final byte	PLAYER_BASE_BOMBS		= 1;
		public static Random		rand					= new Random();
		
		public static int BOMB_TTL()
		{
			return rand.nextInt(1500) + 4500;
		}
		
		public static class StandardField
		{
			public static final int	WOOD			= 60;
			public static final int	WOOD_TO_SPECIAL	= 70;
			public static final int	SIZE			= 11;
		}
	}
	
	public static class NetworkConsts
	{
		public static final long	UPDATE_INTERVAL	= 50L;
		public static final int		SERVER_PORT		= 5636;
	}
	
	public static class PlayerState
	{
		public static final byte	INVALID			= 0;
		public static final byte	RESPAWNED		= 1;
		public static final byte	RUNNING_N		= 2;
		public static final byte	RUNNING_N2		= 3;
		public static final byte	RUNNING_E		= 4;
		public static final byte	RUNNING_E2		= 5;
		public static final byte	RUNNING_S		= 6;
		public static final byte	RUNNING_S2		= 7;
		public static final byte	RUNNING_W		= 8;
		public static final byte	RUNNING_W2		= 9;
		public static final byte	IDLE			= 10;
		public static final byte	IDLE2			= 11;
		public static final byte	STUNNED			= 12;
		public static final byte	PLACING_BOMB	= 13;
		public static String[]		Names			= { "invalid", "hans_respawned", "hans_running_n", "hans_running_n2", "hans_running_e", "hans_running_e2", "hans_running_s", "hans_running_s2", "hans_running_w", "hans_running_w2", "hans_idle", "hans_idle2", "hans_stunned", "hans_bombing" };
	}
}
